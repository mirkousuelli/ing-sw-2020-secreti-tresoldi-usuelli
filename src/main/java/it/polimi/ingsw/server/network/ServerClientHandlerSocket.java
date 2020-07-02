package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedGame;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.game.states.PreparePayload;
import it.polimi.ingsw.server.observer.Observable;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which implements the client-server handling as defined in its interface
 */
public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private final Socket socket;
    private final FileXML file;
    private final ServerConnectionSocket server;

    private final LinkedList<Demand> buffer;

    private boolean isActive;
    private boolean isConnected;
    private boolean creator;
    private boolean loggingOut;
    private boolean isOkToRestart;
    private boolean isToRestart;

    private String name;

    final Object lockRestart = new Object();

    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());

    /**
     * Constructor which establishes server side connection handling each client
     *
     * @param socket server socket for the client handling
     * @param server server main socket
     */
    public ServerClientHandlerSocket(Socket socket, ServerConnectionSocket server) throws IOException {
        this.socket = socket;
        this.server = server;

        file = new FileXML(socket);
        buffer = new LinkedList<>();

        creator = false;
        loggingOut = false;
        isOkToRestart = false;
        isToRestart = false;

        name = null;
    }


    /*-----------------------------------------------GETTER-----------------------------------------------------------*/

    /**
     * Method that pops the first demand inside the buffer
     *
     * @return {@code Demand} first demand to be processed inside the buffer
     */
    private Demand getDemand() {
        Demand demand;

        synchronized (buffer) {
            demand = buffer.removeFirst();
        }

        return demand;
    }

    @Override
    public String getName() {
        return name;
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-----------------------------------------------SETTER-----------------------------------------------------------*/

    /**
     * Method that sets if the connection is active or not
     *
     * @param isActive for saying connection status
     */
    synchronized void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    private synchronized void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * Method that sets the player that created the game
     *
     * @param creator saying if this connection is reported to the creator
     */
    void setCreator(boolean creator) {
        this.creator = creator;
    }

    /**
     * Method that makes connection log out
     *
     * @param loggingOut for saying if logged out
     */
    void setLoggingOut(boolean loggingOut) {
        this.loggingOut = loggingOut;
    }

    void setOkToRestart(boolean okToRestart) {
        synchronized (lockRestart) {
            isOkToRestart = okToRestart;
        }
    }

    void setIsToRestart(boolean toRestart) {
        synchronized (this) {
            this.isToRestart = toRestart;
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/




    /*-----------------------------------------------PREDICATE--------------------------------------------------------*/

    /**
     * Method that check if the buffer has a demand (or is empty otherwise)
     *
     * @return {@code true} demand to be processed, {@code false} demand buffer empty
     */
    private boolean hasDemand() {
        boolean ret;

        synchronized (buffer) {
            ret = !buffer.isEmpty();
        }

        return ret;
    }

    /**
     * Method that says if the connection is active
     *
     * @return {@code true} if the connection is active, {@code false} if it's not
     */
    synchronized boolean isActive() {
        return isActive;
    }

    private synchronized boolean isConnected() {
        return isConnected;
    }

    /**
     * Method that checks if the connection belongs to the creator
     *
     * @return {@code true} if connection belongs to the creator, {@code false} it doesn't belong to the creator
     */
    @Override
    public boolean isCreator() {
        return creator;
    }

    boolean isOkToRestart() {
        boolean isOkToRestartCopy;

        synchronized (lockRestart) {
            isOkToRestartCopy = isOkToRestart;
        }

        return isOkToRestartCopy;
    }

    boolean isToRestart() {
        boolean isToRestartCopy;

        synchronized (this) {
            isToRestartCopy = isToRestart;
        }

        return isToRestartCopy;
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-----------------------------------------------SOCKET-----------------------------------------------------------*/

    /**
     * Method that defines a synchronous sending type of answer
     *
     * @param message server answer
     */
    @Override
    public void send(Answer message) {
        synchronized (file.lockSend) {
            file.send(message);
            }

        if (isToRestart() && isConnected()) {
            synchronized (lockRestart) {
                setOkToRestart(true);
                setIsToRestart(false);
                lockRestart.notifyAll();
            }
        }
    }

    /**
     * Method that reads and notify the controller about the new demand request that is received
     *
     * @return {@code Demand} last demand received
     */
    private Demand read() {
        Demand demand;

        synchronized (file.lockReceive) {
            LOGGER.info("Receiving...");
            demand = (Demand) file.receive();
        }
        LOGGER.info("Received!");

        if (demand == null) {
            setActive(false);
            callWatchDog(false);
        } else if (demand.getPayload() != null && demand.getPayload().toString().equals("close") && !server.getLobby().getGame().getState().getName().equals("victory")) {
            setActive(false);
            callWatchDog(true);
        }

        return demand;
    }

    /**
     * Method that closes the connection
     */
    void closeSocket() {
        try {
            LOGGER.info("Closing the socket...");
            socket.close();
            callWatchDog(false);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, cannot close the socket", e);
        }

        LOGGER.info("Socket closed");
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-----------------------------------------------THREAD-----------------------------------------------------------*/

    /**
     * Method that defines an asynchronous sending type of answer
     *
     * @param message server answer
     */
    @Override
    public void asyncSend(Answer message) {
        new Thread(() -> send(message)).start();
    }

    /**
     * Method which starts running a thread for the reading task
     *
     * @return {@code Thread} reading thread
     */
    private Runnable asyncReadFromSocket(CountDownLatch countDownLatch) {
        return () -> {
            try {
                do {
                    initialization(); //connect -> create/join/reload
                    matchRoutine(); // normal game flow -> new game -> normal game flow -> ...
                } while (isActive());
            } catch (InterruptedException e) {
                if (isActive())
                    LOGGER.log(Level.SEVERE, e, () -> "asyncReadFromSocketThread: Failed to receive!");
                setActive(false);
                Thread.currentThread().interrupt();
            } finally {
                countDownLatch.countDown();
            }
        };
    }

    /**
     * Method that starts running a notifier thread for the controller side in order to support
     * the reading thread
     *
     * @return {@code Thread} new notifier thread
     */
    private Runnable notifierThread(CountDownLatch countDownLatch) {
        return () -> {
            Demand demand;
            Lobby lobby;

            try {
                while (isActive()) {
                    demand = waitDemand();

                    lobby = server.getLobby();
                    if (lobby == null) {
                        setActive(false);
                        break;
                    }

                    notifyDemand(lobby, demand);
                }
            } catch (InterruptedException e) {
                if (isActive())
                    LOGGER.log(Level.SEVERE, e, () -> "notifierThread: Failed to receive!");
                setActive(false);
                Thread.currentThread().interrupt();
            } finally {
                countDownLatch.countDown();
            }
        };
    }

    /**
     * Method that starts running a thread which checks connection active state
     *
     * @return {@code Thread} new active checker thread
     */
    private Runnable watchDogThread() {
        return () -> {
            try {
                synchronized (this) {
                    while (isActive()) this.wait();
                    setActive(false);
                }
            } catch (InterruptedException e) {
                if (isActive())
                    LOGGER.log(Level.SEVERE, e, () -> "watchDogThread: Failed to receive!");
                setActive(false);
                Thread.currentThread().interrupt();
            }
        };
    }

    /**
     * Method that start running the main class thread incorporating the minor ones
     */
    @Override
    public void run() {
        setActive(true);
        setConnected(true);

        try {
            Thread readerThread;
            Thread notifierThread;
            Thread watchDogThread;

            do {
                setActive(isConnected());
                setCreator(false);
                CountDownLatch countDownLatch = new CountDownLatch(2);

                readerThread = new Thread(asyncReadFromSocket(countDownLatch));
                notifierThread = new Thread(notifierThread(countDownLatch));
                watchDogThread = new Thread(watchDogThread());

                readerThread.start();
                notifierThread.start();
                watchDogThread.start();

                watchDogThread.join();
                readerThread.interrupt();
                notifierThread.interrupt();

                countDownLatch.await();
            } while (isConnected());
        } catch (InterruptedException e) {
            if (isActive())
                LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
            Thread.currentThread().interrupt();
        } finally {
            setActive(false);
            setConnected(false);
            if (!loggingOut) {
                server.suddenDisconnection(this);
                LOGGER.info(() -> name + " sudden disconnection!");
            } else
                server.removeFromPending(this);
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*-----------------------------------------------THREAD HELPER----------------------------------------------------*/
    private Demand waitDemand() throws InterruptedException {
        Demand demand;

        synchronized (buffer) {
            while (!hasDemand()) buffer.wait();
            demand = getDemand();
        }

        return demand;
    }

    private void notifyDemand(Lobby lobby, Demand demand) {
        if (!isActive()) return;

        LOGGER.info("Notifying...");
        synchronized (lobby.getController()) {
            notify(demand);
        }
        LOGGER.info("Notified");
    }

    private void matchRoutine() {
        Demand demand;
        boolean newGame;
        Lobby lobby = server.getLobby();

        while (isActive()) {
            demand = read();

            if (!isActive())
                return;

            synchronized (lobby.lockLobby) {
                newGame = lobby.getGame().getState().getName().equals(State.VICTORY.toString());
            }

            if (newGame) { //newGame
                newGame(demand);
                break;
            } else { //normal gameFlow
                LOGGER.info("Consuming...");
                synchronized (buffer) {
                    buffer.addLast(demand);
                    buffer.notifyAll();
                }
                LOGGER.info("Consumed!");
            }
        }
    }

    /**
     * Method that allows to log in and then proceed to start a game or reload a previous one (if there is a backup)
     *
     * @throws InterruptedException if the thread has been interrupted in some way
     */
    private void initialization() throws InterruptedException {
        boolean reload;

        if (!isActive()) return;

        logIn(); //connect

        synchronized (server) {
            reload = server.isLobbyReloaded();
        }

        if (!reload)
            basicInitialization(); //create or join
        else
            reloadStart(); //reload
    }


    /**
     * Method that allows the basic initialization of a game, either by creating it (if nobody has already done it) or
     * by joining it otherwise. It then waits for players to join and when the correct number of player is reached the
     * game begins.
     *
     * @throws InterruptedException if the thread has been interrupted in some way
     */
    private void basicInitialization() throws InterruptedException {
        if (creator) //createGame
            numberOfPlayers();
        else //joinGame
            waitNumberOfPlayers();

        waitStart(); //wait other players

        basicStart(); //start
    }

    /**
     * Method that handles the waiting for the game to have the correct number of players in it
     */
    private void waitNumberOfPlayers() throws InterruptedException {
        Lobby lobby = server.getLobby();

        waitStart();

        synchronized (lobby.lockLobby) {
            if (lobby.isFull() && !lobby.isPresentInGame(name)) {
                setLoggingOut(true);
                closeSocket();
            }
        }
    }

    /**
     * Method which defines the reloaded beginning from a previous match still being played by the same players
     */
    private void reloadStart() throws InterruptedException {
        ReducedGame reducedGame;
        Lobby lobby = server.getLobby();

        waitStart(); //wait other players

        synchronized (lobby.lockLobby) {
            lobby.lockLobby.notifyAll();
            reducedGame = new ReducedGame(lobby);
        }

        //reload
        send(new Answer<>(AnswerType.RELOAD, reducedGame));

        //resume game
        synchronized (lobby.lockLobby) {
            if (lobby.isCurrentPlayerInGame(this)) {
                List<ReducedAnswerCell> payload = preparePayload(lobby);
                send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.BOARD, payload));
            }
        }
    }

    private List<ReducedAnswerCell> preparePayload(Lobby lobby) {
        List<ReducedAnswerCell> payload = new ArrayList<>();
        Game loadedGame = lobby.getGame();

        if (loadedGame.getState() == null) return payload;

        switch (loadedGame.getState().getName()) {
            case "chooseWorker":
                break;

            case "move":
                payload = PreparePayload.preparePayloadMove(loadedGame, Timing.DEFAULT, State.CHOOSE_WORKER);
                break;

            case "build":
                payload = PreparePayload.preparePayloadBuild(loadedGame, Timing.DEFAULT, State.MOVE);
                break;

            case "additionalPower":
                if (lobby.getGame().getPlayer(name).getCard().getPower(0).getEffect().equals(Effect.MOVE))
                    payload = PreparePayload.preparePayloadMove(loadedGame, Timing.ADDITIONAL, State.MOVE);
                else if (lobby.getGame().getPlayer(name).getCard().getPower(0).getEffect().equals(Effect.BUILD))
                    payload = PreparePayload.preparePayloadBuild(loadedGame, Timing.ADDITIONAL, State.BUILD);
                break;

            case "askAdditionalPower":
                if (lobby.getGame().getPlayer(name).getCard().getPower(0).getEffect().equals(Effect.MOVE))
                    payload = PreparePayload.mergeReducedAnswerCellList(PreparePayload.preparePayloadMove(loadedGame, Timing.ADDITIONAL, State.ADDITIONAL_POWER), PreparePayload.preparePayloadBuild(loadedGame, Timing.DEFAULT, State.MOVE));
                else if (lobby.getGame().getPlayer(name).getCard().getPower(0).getEffect().equals(Effect.BUILD))
                    payload = PreparePayload.mergeReducedAnswerCellList(PreparePayload.preparePayloadBuild(loadedGame, Timing.ADDITIONAL, State.BUILD), PreparePayload.preparePayloadBuild(loadedGame, Timing.DEFAULT, State.BUILD));
                break;

            default:
                break;
        }

        loadedGame.setAllowedActions(payload);

        return payload;
    }

    /**
     * Method that operates the log in feature
     */
    private void logIn() {
        Demand demand = null;
        boolean toRepeat = false;

        do {
            if (name == null || toRepeat) { //after a new game or if the name chosen by the player is already taken
                demand = read();
                if (demand != null) //first time playing the game
                    name = ((ReducedMessage) demand.getPayload()).getMessage();
            }

            if (!isActive() || !isConnected()) return;

            if (demand != null || name != null) {
                synchronized (server) {
                    toRepeat = server.connect(this, name); //connect
                }
            }
        } while (toRepeat);
    }

    /**
     * Method that manages the number of players through the demand received
     */
    private void numberOfPlayers() {
        Demand demand;
        boolean toRepeat;

        do {
            demand = read();

            synchronized (server) {
                toRepeat = server.numOfPlayers(this, demand);
            }
        } while (toRepeat);
    }

    /**
     * Method which stops the server in waiting status
     */
    private void waitStart() throws InterruptedException {
        synchronized (server) {
            while (!server.canStart()) server.wait();
        }
    }

    /**
     * Method which defines the standard beginning of a match
     */
    private void basicStart() {
        //wait
        Lobby lobby = server.getLobby();
        List<ReducedPlayer> players;

        synchronized (lobby.lockLobby) {
            lobby.lockLobby.notifyAll();
            players = lobby.getReducedPlayerList();
        }

        send(new Answer<>(AnswerType.SUCCESS, players));
        synchronized (lobby.lockLobby) {
            if (isCreator())
                send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.GOD, lobby.getGame().getDeck().popAllGods(lobby.getNumberOfPlayers())));
        }
    }

    /**
     * Method that creates a new game after the proper demand is received
     */
    private void newGame(Demand demand) {
        boolean toRepeat = false;

        do {
            if (toRepeat)
                demand = read();

            synchronized (server) {
                toRepeat = server.newGame(this, demand);
            }

        } while (toRepeat);
    }


    void callWatchDog(boolean connected) {
        synchronized (this) {
            setActive(false);
            setConnected(connected);
            this.notifyAll();
            LOGGER.info(() -> name + " watch dog called...");
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/
}
