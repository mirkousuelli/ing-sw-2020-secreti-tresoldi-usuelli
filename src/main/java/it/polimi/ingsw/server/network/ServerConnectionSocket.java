package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.message.Lobby;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectionSocket implements ServerConnection {
    private final int port;
    private static final String BACKUPPATH = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_lobby.xml";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private Lobby lobby;
    private boolean isActive;

    public ServerConnectionSocket(int port) {
        this.port = port;

        lobby = null;
        loadLobby();

        isActive = false;
    }

    private void loadLobby() {
        Game loadedGame = null;

        try {
            File f = new File(BACKUPPATH);
            if (f.exists()) {
                loadedGame = GameMemory.load(BACKUPPATH);
                if (loadedGame.getState().getName().equals(State.VICTORY.toString()))
                    loadedGame = null;
            }
        } catch (ParserConfigurationException | SAXException e) {
            LOGGER.log(Level.SEVERE, "Cannot load backup", e);
        }

        if (loadedGame != null) {
            lobby = new Lobby(loadedGame);
            lobby.setNumberOfPlayers(loadedGame.getNumPlayers());
        }
    }

    @Override
    public void startServer() throws IOException {
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ServerClientHandlerSocket handler;
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, port not available", e); //port not available
            return;
        }

        LOGGER.info("Server ready");

        isActive = true;
        while (isActive) {
            try {
                socket = serverSocket.accept();
                handler = new ServerClientHandlerSocket(socket, this);
                executor.submit(handler);
            }
            catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Got an Exception, serverSocket closed", e);
                break; //In case the serverSocket gets closed
            }
        }

        executor.shutdown();
        if (socket != null) socket.close();
        serverSocket.close();
    }

    @Override
    public void deregisterConnection(ServerClientHandler c) {
        for (ServerClientHandler ch : lobby.getServerClientHandlerList()) {
            ch.asyncSend(new Answer(AnswerType.SUCCESS, new ReducedPlayer(lobby.getPlayer(c))));
        }

        c.setActive(false);
        lobby.deletePlayer(c);
    }

    @Override
    public void SuddenDisconnection() {
        for (ServerClientHandler ch : lobby.getServerClientHandlerList()) {
            ch.closeSocket();
        }

        lobby.setNumberOfReady(0);
        File f = new File(Lobby.backupPath);
        if (f.exists())
            lobby.setReloaded(true);
        else
            lobby = null;
    }

    @Override
    public synchronized void connect(ServerClientHandler c, String name) throws ParserConfigurationException, SAXException {
        if (lobby != null && lobby.isReloaded() && lobby.getGame().getPlayer(name) != null) {
            //reload
            lobby.addPlayer(name, c);
            c.setLobby(lobby);
            LOGGER.info("Reloaded!");
        }
        else if (lobby == null || (lobby.isReloaded() && lobby.getGame().getPlayer(name) == null && lobby.getNumberOfReady() == 0)) {
            //create
            lobby = new Lobby();
            lobby.addPlayer(name, c);
            lobby.setCurrentPlayer(lobby.getReducedPlayerList().get(0).getNickname());
            c.setLobby(lobby);
            c.setCreator(true);
            LOGGER.info("Created!");

            c.send(new Answer<>(AnswerType.CHANGE_TURN, new ReducedPlayer(lobby.getPlayer(c), lobby.getColor(c), true)));
        }
        else if (!lobby.isReloaded() && lobby.getGame().getPlayer(name) == null &&
                 lobby.getNumberOfPlayers() != -1 && lobby.getNumberOfPlayers() > lobby.getReducedPlayerList().size()) {
            //join
            lobby.addPlayer(name, c);
            c.setLobby(lobby);
            c.send(new Answer<>(AnswerType.CHANGE_TURN, new ReducedPlayer(lobby.getPlayer(c), lobby.getColor(c))));
            LOGGER.info("Joined!");
        }

        if (lobby.isReloaded() && lobby.getGame().getPlayer(name) == null && lobby.getNumberOfReady() != 0) { //reloaded lobby is full
            c.setLoggingOut(true);
            c.closeSocket();
        }
        else
            c.setLobby(lobby);
    }

    @Override
    public synchronized boolean numOfPlayers(ServerClientHandler c, Demand demand) {
        Lobby lobby;
        String value = ((ReducedMessage) demand.getPayload()).getMessage();
        int numOfPls = Integer.parseInt(value);

        if (demand.getHeader() == DemandType.CREATE_GAME) {
            lobby = c.getLobby();

            if (numOfPls == 2 || numOfPls == 3) {
                lobby.setNumberOfPlayers(numOfPls);
                synchronized (lobby.lockLobby) {
                    lobby.lockLobby.notifyAll();
                }
                return false;
            }
        }

        c.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    @Override
    public synchronized boolean newGame(ServerClientHandler c, Demand demand) {
        String response = ((ReducedMessage) demand.getPayload()).getMessage();

        if (response.equals("n")) {
            if (c.isCreator() && lobby.getNumberOfReady() > 1)
                lobby.setNewCreator(c).setCreator(true);

            lobby.deletePlayer(c);
            lobby.setNumberOfReady(lobby.getNumberOfReady() - 1);
            c.setLoggingOut(true);
            c.closeSocket();
            return false;
        }
        else if (response.equals("y")) {
            lobby.setNumberOfReady(lobby.getNumberOfReady() + 1);

            if (lobby.getNumberOfReady() == 2 * lobby.getNumberOfPlayers()) {
                lobby.getGame().setState(State.START);
                lobby.setNumberOfReady(lobby.getNumberOfPlayers());
            }
            return false;
        }

        c.send(new Answer(AnswerType.ERROR));
        System.out.println("Error");
        return true;
    }
}
