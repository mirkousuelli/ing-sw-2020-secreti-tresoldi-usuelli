package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;
import it.polimi.ingsw.communication.message.payload.ReducedGame;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.network.message.Lobby;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private final Socket socket;
    private final FileXML file;
    private final ServerConnection server;
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());
    Lobby lobby;
    private final List<Demand> buffer;

    private boolean isActive;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server, String pathFile) throws FileNotFoundException {
        this.socket = socket;
        this.server = server;
        file = new FileXML(pathFile, socket);
        lobby = null;
        buffer = new LinkedList<>();
    }

    private synchronized boolean isActive(){
        return isActive;
    }

    public synchronized void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    private boolean hasDemand() {
        boolean ret;

        synchronized (buffer) {
            ret = buffer.size() > 0;
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

    private void send(Answer message) {
        synchronized (file.lockSend) {
            try {
                file.send(message);    // INCAPSULATO
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Got an IOException", e);
            }
        }
    }

    @Override
    public void closeConnection() {
        try {
            socket.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, cannot close the socket", e);
        }
    }

    private void close() {
        closeConnection();
        LOGGER.info("Deregistering client...");
        synchronized (server) {
            server.deregisterConnection(this);
        }
        LOGGER.info("Done");
    }

    @Override
    public void asyncSend(Answer message) {
        new Thread( () -> send(message) ).start();
    }

    private Demand read() throws IOException {
        Demand demand;

        synchronized (file.lockReceive) {
            LOGGER.info("Receiving...");
            demand = (Demand) file.receive();
        }
        LOGGER.info("Received!");

        return demand;
    }

    private Thread asyncReadFromSocket() {
        Thread t = new Thread(
                () -> {
                        try {
                            Demand demand;
                            boolean toRepeat;

                            //connect
                            demand = read();
                            synchronized (server) {
                                server.connect(this, ((ReduceDemandChoice) demand.getPayload()).getChoice());
                            }

                            boolean reload = false;
                            if (lobby != null) {
                                synchronized (lobby.lockLobby) {
                                    reload = lobby.isReloaded();
                                }
                            }

                            if(!reload) {
                                //createGame or askLobby
                                do {
                                    demand = read();
                                    synchronized (server) {
                                        toRepeat = server.prelobby(this, demand);
                                    }
                                } while (toRepeat);

                                //wait or join game
                                boolean join = false;
                                do {
                                    demand = read();
                                    if (demand.getHeader().equals(DemandType.ASK_LOBBY))
                                        join = true;
                                    synchronized (server) {
                                        toRepeat = server.lobby(this, demand);
                                    }
                                } while (toRepeat);

                                synchronized (lobby.lockLobby) {
                                    join = !lobby.canStart() && join;
                                }

                                //send a wait to anyone except the last one
                                if (join)
                                    asyncSend(new Answer(AnswerType.SUCCESS, DemandType.WAIT, new ReduceDemandChoice("wait")));

                                //wait
                                List<ReducedPlayer> players;
                                synchronized (lobby.lockLobby) {
                                    while (!lobby.canStart()) lobby.lockLobby.wait();
                                    synchronized (lobby.lockLobby) {
                                        lobby.lockLobby.notifyAll();
                                        players = lobby.getReducedPlayerList();
                                        lobby.setCurrentPlayer(players.get(0).getNickname());
                                    }
                                }

                                //start
                                asyncSend(new Answer(AnswerType.SUCCESS, DemandType.START, players));
                                LOGGER.info(() -> "Names: " + players.get(0).getNickname() + ", " + players.get(1).getNickname());
                                synchronized (lobby.lockLobby) {
                                    if (lobby.isCurrentPlayerInGame(this)) {
                                        synchronized (buffer) {
                                            buffer.add(new Demand(DemandType.CHOOSE_CARD, ""));
                                        }
                                    }

                                }
                            }
                            else {
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
                                asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.RELOAD, reducedGame));

                                //resume game
                                synchronized (lobby.lockLobby) {
                                    if (lobby.isCurrentPlayerInGame(this))
                                        asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.parseString(loadedGame.getState().getName()), new ReduceDemandChoice("resume")));
                                }
                            }


                            while(isActive()) {
                                demand = read();
                                LOGGER.info("Consuming...");
                                synchronized (buffer) {
                                    buffer.add(demand);
                                    buffer.notifyAll();
                                }
                                LOGGER.info("Consumed!");
                            }
                        } catch (NoSuchElementException | ParserConfigurationException | SAXException | IOException | InterruptedException e) {
                            LOGGER.log(Level.SEVERE, e, () -> "Failed to receive!!" + e.getMessage());
                            setActive(false);
                        }
                }
        );
        t.start();
        return t;
    }

    private Thread notifierThred() {
        Thread t = new Thread(
                () -> {
                    try {
                        Demand demand;
                        while (isActive) {
                            synchronized (buffer) {
                                while (!hasDemand()) buffer.wait();
                                demand = getDemand();
                            }

                            LOGGER.info("Notifying...");
                            notify(demand);
                            LOGGER.info("Notified");
                        }
                    } catch (InterruptedException e){
                        setActive(false);
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
            Thread notifier = notifierThred();
            reader.join();
            notifier.join();
        } catch (InterruptedException | NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Connection closed from the client side", e);
        } finally {
            setActive(false);
            closeConnection();
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
}
