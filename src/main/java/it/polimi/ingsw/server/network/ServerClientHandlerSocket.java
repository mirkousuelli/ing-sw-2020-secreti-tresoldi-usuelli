package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.server.network.message.Lobby;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
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

    private boolean active;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server, String pathFile) throws FileNotFoundException {
        this.socket = socket;
        this.server = server;
        file = new FileXML(pathFile, socket);
    }

    private synchronized boolean isActive(){
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    private void send(Message message) {
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
    public void asyncSend(final Message message) {
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

    @Override
    public void run() {
        setActive(true);

        try {
            Demand demand;
            boolean toRepeat;

            //connect
            demand = read();
            synchronized (server) {
                server.connect(this, (String) demand.getPayload());
            }

            //createGame or askLobby
            do {
                demand = read();
                synchronized (server) {
                    toRepeat = server.prelobby(demand, this);
                }
            } while (toRepeat);

            //wait or join game
            boolean join = false;
            do {
                demand = read();
                if (demand.getHeader().equals(DemandType.ASK_LOBBY))
                    join = true;
                synchronized (server) {
                    toRepeat = server.lobby(demand, this);
                }
            } while (toRepeat);

            if (join)
                asyncSend(new Answer(AnswerType.SUCCESS, DemandType.WAIT, ""));

            //wait
            List<ReducedPlayer> players;
            synchronized (lobby.lockLobby) {
                while (!lobby.canStart()) lobby.lockLobby.wait();
                synchronized (lobby.lockLobby) {
                    lobby.lockLobby.notifyAll();
                    players = lobby.getReducedPlayerList();
                    lobby.setCurrentPlayer(players.get(0).getNickname());
                }

                //start
                asyncSend(new Answer(AnswerType.SUCCESS, DemandType.START, players));
                LOGGER.info(() -> "Names: " + players.get(0).getNickname() + ", " + players.get(1).getNickname());
            }

            while(isActive()) {
                demand = read();
                notify(demand);
                LOGGER.info(LOGGER.getName() + "Notified!");
            }
        } catch (NoSuchElementException | ParserConfigurationException | SAXException | IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Failed to receive!!" + e.getMessage(), e);
        } finally {
            setActive(false);
            close();
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
