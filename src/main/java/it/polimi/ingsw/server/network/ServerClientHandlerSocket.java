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
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.game.states.PreparePayload;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final AtomicInteger numOfThreadDone = new AtomicInteger();

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

    /**
     * Method that gets connection player's name
     *
     * @return {@code String} player's name
     */
    @Override
    public String getName() {
        return name;
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-----------------------------------------------SETTER-----------------------------------------------------------*/

    /**
     * Method that sets if the connection is activer or not
     *
     * @param isActive for saying connection status
     */
    @Override
    public synchronized void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    private synchronized void setConnected(boolean connected) {
        isConnected = connected;
    }

    /**
     * Method that sets the player game creator
     *
     * @param creator saying if this connection is reported to the creator
     */
    @Override
    public void setCreator(boolean creator) {
        this.creator = creator;
    }

    /**
     * Method that makes connection log out
     *
     * @param loggingOut for saying if logged out
     */
    @Override
    public void setLoggingOut(boolean loggingOut) {
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
     * Method that check demand buffer
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
     * Method that says if the connection is actived
     *
     * @return {@code true} connection active, {@code false} connection not active
     */
    @Override
    public synchronized boolean isActive() {
        return isActive;
    }

    private synchronized boolean isConnected() {
        return isConnected;
    }

    /**
     * Method that checks if the connection belong to the creator
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
            try {
                file.send(message);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Got an IOException", e);
            }
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
     * Method that reads and notify the controller about new demand request received
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

        if (demand == null)
            callWatchDog(false);

        return demand;
    }

    /**
     * Method that close the connection
     */
    @Override
    public void closeSocket() {
        try {
            LOGGER.info("Closing the socket...");
            socket.close();
            synchronized (this) {
                setActive(false);
                setConnected(false);
                this.notifyAll();
            }
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
     * Method which start running a thread for the reading task
     *
     * @return {@code Thread} reading thread
     */
    private Runnable asyncReadFromSocket() {
        return () -> {
            try {
                do {
                    initialization(); //connect -> create/join/reload
                    matchRoutine(); // normal game flow -> new game -> normal game flow -> ...
                } while (isActive());
            } catch (InterruptedException e) {
                if (isActive())
                    LOGGER.log(Level.INFO, e, () -> "asyncReadFromSocketThread: Failed to receive!");
                Thread.currentThread().interrupt();
                setActive(false);

                synchronized (numOfThreadDone) {
                    numOfThreadDone.getAndIncrement();
                    numOfThreadDone.notifyAll();
                }
            }
        };
    }

    /**
     * Method that start running a notifier thread for the controller side in order to support
     * the reading thread
     *
     * @return {@code Thread} new notifier thread
     */
    private Runnable notifierThread() {
        return () -> {
            Demand demand;
            Lobby lobby;

            try {
                while (isActive()) {
                    synchronized (buffer) {
                        while (!hasDemand()) buffer.wait();
                        demand = getDemand();
                    }

                    if (!isActive())
                        break;

                    lobby = server.getLobby();
                    if (lobby == null) {
                        setActive(false);
                        break;
                    }

                    LOGGER.info("Notifying...");
                    synchronized (lobby.getController()) {
                        notify(demand);
                    }
                    LOGGER.info("Notified");
                }
            } catch (InterruptedException e) {
                if (isActive())
                    LOGGER.log(Level.INFO, e, () -> "notifierThread: Failed to receive!");
                Thread.currentThread().interrupt();
                setActive(false);

                synchronized (numOfThreadDone) {
                    numOfThreadDone.getAndIncrement();
                    numOfThreadDone.notifyAll();
                }
            }
        };
    }

    /**
     * Method that start running a thread which checks connection active state
     *
     * @return {@code Thread} new active checker thread
     */
    private Runnable watchDogThread() {
        return () -> {
            try {
                synchronized (this) {
                    while (isActive()) this.wait();
                }
            } catch (InterruptedException e) {
                if (isActive())
                    LOGGER.log(Level.INFO, e, () -> "watchDogThread: Failed to receive!");
                Thread.currentThread().interrupt();
                setActive(false);
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

                readerThread = new Thread(asyncReadFromSocket());
                notifierThread = new Thread(notifierThread());
                watchDogThread = new Thread(watchDogThread());

                readerThread.start();
                notifierThread.start();
                watchDogThread.start();

                watchDogThread.join();
                readerThread.interrupt();
                notifierThread.interrupt();

                synchronized (numOfThreadDone) {
                    while (numOfThreadDone.get() < 2) numOfThreadDone.wait();
                }
                numOfThreadDone.set(0);
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
            }
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*-----------------------------------------------THREAD HELPER----------------------------------------------------*/
    private void matchRoutine() {
        Demand demand;
        boolean newGame;
        Lobby lobby = server.getLobby();

        while (isActive()) {
            demand = read();

            synchronized (lobby.lockLobby) {
                newGame = lobby.getGame().getState().getName().equals(State.VICTORY.toString());
                lobby.setReloaded(false);
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

    private void initialization() throws InterruptedException {
        boolean reload;

        logIn(); //connect

        synchronized (server) {
            reload = server.isLobbyReloaded();
        }

        if (!reload)
            basicInitialization(); //create or join
        else
            reloadStart(); //reload
    }


    private void basicInitialization() throws InterruptedException {
        if (creator) //createGame
            numberOfPlayers();
        else //joinGame
            waitNumberOfPlayers();

        waitStart(); //wait other players

        basicStart(); //start
    }

    /**
     * Method that manage the waiting flow for the joining initial part of the application
     */
    private void waitNumberOfPlayers() throws InterruptedException {
        Lobby lobby = server.getLobby();

        waitStart();

        synchronized (lobby.lockLobby) {
            if (lobby.isFull() && !lobby.isPresentInGame(this)) {
                setLoggingOut(true);
                closeSocket();
            }
        }
    }

    /**
     * Method which defines the reloaded beginning from a previous match still in playing byt the same players
     */
    private void reloadStart() throws InterruptedException {
        ReducedGame reducedGame;
        Game loadedGame;
        Lobby lobby = server.getLobby();

        waitStart(); //wait other players

        synchronized (lobby.lockLobby) {
            lobby.lockLobby.notifyAll();
            loadedGame = lobby.getGame();
            reducedGame = new ReducedGame(lobby);
        }

        //reload
        send(new Answer<>(AnswerType.RELOAD, reducedGame));

        //resume game
        synchronized (lobby.lockLobby) {
            if (lobby.isCurrentPlayerInGame(this)) {
                List<ReducedAnswerCell> payload = new ArrayList<>();

                if (loadedGame.getState().getName().equals(State.MOVE.toString()))
                    payload = PreparePayload.preparePayloadMove(loadedGame, Timing.DEFAULT, State.CHOOSE_WORKER);
                else if (loadedGame.getState().getName().equals(State.BUILD.toString()))
                    payload = PreparePayload.preparePayloadBuild(loadedGame, Timing.DEFAULT, State.MOVE);
                else if (loadedGame.getState().getName().equals(State.ADDITIONAL_POWER.toString())) {
                    if (loadedGame.getPrevState().equals(State.MOVE))
                        payload = PreparePayload.preparePayloadMove(loadedGame, Timing.ADDITIONAL, State.MOVE);
                    if (loadedGame.getPrevState().equals(State.BUILD))
                        payload = PreparePayload.preparePayloadBuild(loadedGame, Timing.ADDITIONAL, State.BUILD);
                }

                send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.BOARD, payload));
            }
        }
    }


    /**
     * Method that operates the log ing feature
     */
    private void logIn() {
        Demand demand;
        boolean toRepeat;

        if (name != null) return;

        do {
            if (!server.isInWaitingConnectionFromReload(this)) {
                demand = read();

                if (demand == null)
                    break;
                else
                    name = ((ReducedMessage) demand.getPayload()).getMessage();
            }

            synchronized (server) {
                toRepeat = server.connect(this, name);
            }
        } while (toRepeat);
    }

    /**
     * Method that manage the number of players through the demand received
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
     * Method that makes a new game creation after the proper demand received
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
