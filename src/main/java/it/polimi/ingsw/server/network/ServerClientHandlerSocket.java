package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.game.states.ChooseWorker;
import it.polimi.ingsw.server.model.game.states.Move;
import it.polimi.ingsw.server.network.message.Lobby;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private final Socket socket;
    private final FileXML file;
    private final ServerConnection server;
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());
    private Lobby lobby;
    private final List<Demand> buffer;

    private boolean isActive;
    private boolean creator;
    private boolean loggingOut;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server) throws IOException {
        this.socket = socket;
        this.server = server;
        file = new FileXML(socket);
        lobby = null;
        buffer = new LinkedList<>();
        creator = false;
        loggingOut = false;
    }

    @Override
    public void setLoggingOut(boolean loggingOut) {
        this.loggingOut = loggingOut;
    }

    @Override
    public synchronized void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public synchronized boolean isActive() {
        return isActive;
    }

    private boolean hasDemand() {
        boolean ret;

        synchronized (buffer) {
            ret = !buffer.isEmpty();
        }

        return ret;
    }

    private Demand getDemand() {
        Demand demand;

        synchronized (buffer) {
            demand = buffer.remove(0);
        }

        return demand;
    }

    public void send(Answer message) {
        synchronized (file.lockSend) {
            try {
                file.send(message);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Got an IOException", e);
            }
        }
    }

    @Override
    public void closeSocket() {
        try {
           if (!socket.isClosed())
                socket.close();
           synchronized (this) {
               setActive(false);
               notifyAll();
           }
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, cannot close the socket", e);
        }
    }

    @Override
    public void asyncSend(Answer message) {
        new Thread( () -> send(message) ).start();
    }

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

    private Thread asyncReadFromSocket() {
        Thread t = new Thread(
                () -> {
                        try {
                            Demand demand = null;
                            boolean toRepeat;
                            boolean reload;

                            //connect
                            do {
                                if (demand == null)
                                    demand = read();

                                synchronized (server) {
                                    toRepeat = server.connect(this, ((ReducedMessage) demand.getPayload()).getMessage());
                                }

                                if (!creator) {
                                    synchronized (lobby.lockLobby) {
                                        while (lobby.getNumberOfPlayers() == -1) lobby.lockLobby.wait();

                                        if (lobby.getNumberOfPlayers() == lobby.getReducedPlayerList().size() && !lobby.isPresentInGame(this)) {
                                            send(new Answer(AnswerType.CLOSE, DemandType.CONNECT, new ReducedMessage("null")));
                                            setActive(false);
                                            return;
                                        }
                                    }
                                }
                            } while (toRepeat);

                            //createGame
                            if (creator) {
                                do {
                                    demand = read();

                                    synchronized (server) {
                                        toRepeat = server.numOfPlayers(this, demand);
                                    }
                                } while (toRepeat);
                            }

                            synchronized (lobby.lockLobby) {
                                reload = lobby.isReloaded();
                            }

                            if(!reload)
                                basicStart();
                            else
                                reloadStart();

                            boolean newGame;
                            while(isActive) {
                                demand = read();

                                synchronized (lobby.lockLobby) {
                                    newGame = lobby.getGame().getState().getName().equals(State.VICTORY.toString());
                                    lobby.setNumberOfReady(0);
                                    lobby.setReloaded(false);
                                }

                                if (newGame) {
                                    do {
                                        synchronized (server) {
                                            toRepeat = server.newGame(this, demand);
                                        }
                                        if (toRepeat)
                                            demand = read();
                                        else if (!loggingOut)
                                            basicStart();
                                    } while (toRepeat);
                                }
                                else {
                                    LOGGER.info("Consuming...");
                                    synchronized (buffer) {
                                        buffer.add(demand);
                                        buffer.notifyAll();
                                    }
                                    LOGGER.info("Consumed!");
                                }
                            }
                        } catch (Exception e) {
                            if (!(e instanceof InterruptedException))
                                LOGGER.log(Level.INFO, e, () -> "Failed to receive!!" + e.getMessage());
                        }
                }
        );
        t.start();
        return t;
    }

    private Thread notifierThread() {
        Thread t = new Thread(
                () -> {
                    try {
                        Demand demand;
                        while (isActive()) {
                            synchronized (buffer) {
                                while (!hasDemand()) buffer.wait();
                                demand = getDemand();
                            }

                            if (!isActive()) break;

                            LOGGER.info("Notifying...");
                            synchronized (lobby.getController()) {
                                notify(demand);
                            }
                            LOGGER.info("Notified");
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
            if (!loggingOut)
                server.SuddenDisconnection();
        }
    }

    @Override
    public Lobby getLobby() {
        return lobby;
    }

    @Override
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public void setCreator(boolean creator) {
        this.creator = creator;
    }

    private void basicStart() throws InterruptedException {
        //wait
        List<ReducedPlayer> players;
        synchronized (lobby.lockLobby) {
            while (!lobby.canStart()) lobby.lockLobby.wait();
            synchronized (lobby.lockLobby) {
                lobby.lockLobby.notifyAll();
                players = lobby.getReducedPlayerList();
            }
        }

        //start
        send(new Answer(AnswerType.SUCCESS, DemandType.START, players));
        synchronized (lobby.lockLobby) {
            send(new Answer(AnswerType.SUCCESS, DemandType.CHANGE_TURN, new ReducedPlayer(lobby.getGame().getCurrentPlayer().nickName)));
            if (lobby.isCurrentPlayerInGame(this))
                asyncSend(new Answer(AnswerType.SUCCESS, DemandType.CHOOSE_DECK, lobby.getGame().getDeck().popAllGods(lobby.getNumberOfPlayers())));
        }
    }

    private void reloadStart() throws InterruptedException {
        ReducedGame reducedGame;
        Game loadedGame;

        synchronized (lobby.lockLobby) {
            while (!lobby.canStart()) lobby.lockLobby.wait();
            synchronized (lobby.lockLobby) {
                lobby.lockLobby.notifyAll();
                loadedGame = lobby.getGame();
                reducedGame = new ReducedGame(lobby);
            }
        }

        //reload
        send(new Answer<>(AnswerType.SUCCESS, DemandType.RELOAD, reducedGame));

        //resume game
        synchronized (lobby.lockLobby) {
            if (lobby.isCurrentPlayerInGame(this)) {
                List<ReducedAnswerCell> payload = new ArrayList<>();

                if (loadedGame.getState().getName().equals(State.MOVE.toString()))
                    payload = ChooseWorker.preparePayloadMove(loadedGame, Timing.DEFAULT, State.CHOOSE_WORKER);

                if (loadedGame.getState().getName().equals(State.BUILD.toString()))
                    payload = Move.preparePayloadBuild(loadedGame, Timing.DEFAULT, State.MOVE);

                if (loadedGame.getState().getName().equals(State.ADDITIONAL_POWER.toString())) {
                    if (loadedGame.getPrevState().equals(State.MOVE))
                        payload = ChooseWorker.preparePayloadMove(loadedGame, Timing.ADDITIONAL, State.MOVE);
                    if (loadedGame.getPrevState().equals(State.BUILD))
                        payload = Move.preparePayloadBuild(loadedGame, Timing.ADDITIONAL, State.BUILD);
                }

                asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.parseString(loadedGame.getState().getName()), payload));
            }
        }
    }
}
