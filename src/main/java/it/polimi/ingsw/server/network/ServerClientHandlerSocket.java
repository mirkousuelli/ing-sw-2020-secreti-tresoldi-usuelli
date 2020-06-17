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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which implements the client-server handling as defined in its interface
 */
public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private final Socket socket;
    private final FileXML file;
    private final ServerConnectionSocket server;
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());
    private final LinkedList<Demand> buffer;

    private boolean isActive;
    private boolean creator;
    private boolean loggingOut;

    private String name;

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

    /**
     * Method that sets if the connection is activer or not
     *
     * @param isActive for saying connection status
     */
    @Override
    public synchronized void setActive(boolean isActive) {
        this.isActive = isActive;
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
    }

    /**
     * Method that close the connection
     */
    @Override
    public void closeSocket() {
        try {
           if (!socket.isClosed())
                socket.close();
           synchronized (this) {
               setActive(false);
               this.notifyAll();
           }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, cannot close the socket", e);
        }
    }

    /**
     * Method that defines an asynchronous sending type of answer
     *
     * @param message server answer
     */
    @Override
    public void asyncSend(Answer message) {
        new Thread( () -> send(message) ).start();
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

        if (demand == null) {
            synchronized (this) {
                notifyAll();
                setActive(false);
                LOGGER.info("Closing...");
            }
        }

        return demand;
    }

    /**
     * Method which start running a thread for the reading task
     *
     * @return {@code Thread} reading thread
     */
    private Thread asyncReadFromSocket() {
        Thread t = new Thread(
                () -> {
                        boolean reload;
                        boolean newGame;

                        try {
                            do {
                                //connect
                                logIn();

                                if (isToInterrupt())
                                    break;

                                synchronized (server) {
                                    reload = server.isLobbyReloaded();
                                }

                                if (!reload) {
                                    if (creator) //createGame
                                        numberOfPlayers();
                                    else //joinGame
                                        waitNumberOfPlayers();

                                    if (isToInterrupt())
                                        break;

                                    waitStart(); //wait other players

                                    basicStart(); //start
                                } else
                                    reloadStart(); //reload

                                if (isToInterrupt())
                                    break;

                                Demand demand;
                                Lobby lobby = server.getLobby();
                                while (isActive) {
                                    demand = read();

                                    if (isToInterrupt())
                                        break;

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
                            } while (isActive);
                        } catch (Exception e) {
                            if (!(e instanceof InterruptedException))
                                LOGGER.log(Level.INFO, e, () -> "Failed to receive!" + e.getMessage());
                        }
                }
        );

        t.start();
        return t;
    }

    /**
     * Method that start running a notifier thread for the controller side in order to support
     * the reading thread
     *
     * @return {@code Thread} new notifier thread
     */
    private Thread notifierThread() {
        Thread t = new Thread(
                () -> {
                    try {
                        Demand demand;
                        Lobby lobby;

                        while (isActive()) {
                            synchronized (buffer) {
                                while (!hasDemand()) buffer.wait();
                                demand = getDemand();
                            }

                            if (!isActive()) break;

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
                    } catch (Exception e){
                        if (!(e instanceof InterruptedException))
                            LOGGER.log(Level.INFO, e, () -> "Failed to receive!" + e.getMessage());
                    }
                }
        );
        t.start();
        return t;
    }

    /**
     * Method that start running a thread which checks connection active state
     *
     * @return {@code Thread} new active checker thread
     */
    private Thread watchDogThread() {
        Thread t = new Thread(
                () -> {
                    try {
                        synchronized (this) {
                            while (isActive()) this.wait();
                        }
                    } catch (Exception e){
                        if (!(e instanceof InterruptedException))
                            LOGGER.log(Level.INFO, e, () -> "Failed to receive!!" + e.getMessage());
                    }
                }
        );
        t.start();
        return t;
    }

    /**
     * Method that start running the main class thread incorporating the minor ones
     */
    @Override
    public void run() {
        setActive(true);

        try {
            Thread reader = asyncReadFromSocket();
            Thread notifier = notifierThread();
            Thread watchDog = watchDogThread();
            watchDog.join();
            reader.interrupt();
            notifier.interrupt();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
            if (!loggingOut) {
                server.suddenDisconnection();
                LOGGER.info(() -> "sudden disconnection!");
            }
        }
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
     * Method that checks if the connection belong to the creator
     *
     * @return {@code true} if connection belongs to the creator, {@code false} it doesn't belong to the creator
     */
    @Override
    public boolean isCreator() {
        return creator;
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

    /**
     * Method that operates the log ing feature
     */
    private void logIn() {
        Demand demand;
        boolean toRepeat;

        do {
            if (!server.isInWaitingConnectionFromReload(this)) {
                demand = read();

                if (isToInterrupt())
                    return;

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

            if (isToInterrupt())
                return;

            synchronized (server) {
                toRepeat = server.numOfPlayers(this, demand);
            }
        } while (toRepeat);
    }

    /**
     * Method that manage the waiting flow for the joining initial part of the application
     */
    private void waitNumberOfPlayers() throws InterruptedException {
        Lobby lobby = server.getLobby();

        boolean interrupt = waitStart();

        if (interrupt)
            return;

        synchronized (lobby.lockLobby) {
            if (lobby.isFull() && !lobby.isPresentInGame(this)) {
                setLoggingOut(true);
                closeSocket();
            }
        }
    }

    /**
     * Method which stops the server in waiting status
     */
    private boolean waitStart() throws InterruptedException {
        synchronized (server) {
            while (!server.canStart()) server.wait();
        }

        return isToInterrupt();
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

        if (isToInterrupt())
            return;

        //start
        send(new Answer<>(AnswerType.SUCCESS, players));
        synchronized (lobby.lockLobby) {
            if (isCreator())
                send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.GOD, lobby.getGame().getDeck().popAllGods(lobby.getNumberOfPlayers())));
        }
    }

    /**
     * Method which defines the reloaded beginning from a previous match still in playing byt the same players
     */
    private void reloadStart() throws InterruptedException {
        ReducedGame reducedGame;
        Game loadedGame;
        Lobby lobby = server.getLobby();

        boolean interrupt = waitStart(); //wait other players

        if (interrupt)
            return;

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
     * Method that makes a new game creation after the proper demand received
     */
    private void newGame(Demand demand) {
        boolean toRepeat;

        do {
            synchronized (server) {
                toRepeat = server.newGame(this, demand);
            }

            if (toRepeat) {
                demand = read();
                if (isToInterrupt())
                    return;
            }
        } while (toRepeat);
    }

    private boolean isToInterrupt() {
        if ((!isActive && !Thread.currentThread().isInterrupted())) {
            server.deletePlayer(this);
            Thread.currentThread().interrupt();
        }

        return !isActive;
    }
}
