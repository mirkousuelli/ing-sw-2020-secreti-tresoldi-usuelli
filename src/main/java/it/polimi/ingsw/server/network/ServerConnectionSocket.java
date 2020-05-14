package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.message.Lobby;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectionSocket implements ServerConnection {
    private final int port;
    private static final Random random = new SecureRandom();
    private static final String BACKUPPATH = "backup_lobby.xml";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private Lobby lobby;

    public ServerConnectionSocket(int port) {
        this.port = port;

        lobby = null;
        loadLobby();
    }

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

        while (!Thread.currentThread().isInterrupted()) {
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

    //Deregister connection
    @Override
    public void deregisterConnection(ServerClientHandler c) {
        //TODO lobby.stop?
    }

    //Wait for another player
    @Override
    public synchronized boolean connect(ServerClientHandler c, String name) throws ParserConfigurationException, SAXException {
        if (lobby != null && lobby.isReloaded() && lobby.getGame().getPlayer(name) != null) {
            lobby.addPlayer(name, c);
            c.setLobby(lobby);
            LOGGER.info("Reloaded!");
            return false;
        }
        else if (lobby == null || (lobby.isReloaded() && lobby.getGame().getPlayer(name) == null)) {
            lobby = new Lobby();
            lobby.addPlayer(name, c);
            lobby.setCurrentPlayer(lobby.getReducedPlayerList().get(0).getNickname());
            c.setLobby(lobby);
            c.setCreator(true);

            c.send(new Answer<>(AnswerType.SUCCESS, DemandType.CREATE_GAME, new ReducedMessage(lobby.getColor(c))));
            return false;
        }
        else if (!lobby.isReloaded() && lobby.getGame().getPlayer(name) == null &&
                 lobby.getNumberOfPlayers() != -1 && lobby.getNumberOfPlayers() > lobby.getReducedPlayerList().size()) {
            lobby.addPlayer(name, c);
            c.setLobby(lobby);
            c.send(new Answer<>(AnswerType.SUCCESS, DemandType.CONNECT, new ReducedMessage(lobby.getColor(c))));
            return false;
        }

        if (lobby.getNumberOfPlayers() != -1)
            c.send(new Answer<>(AnswerType.ERROR, DemandType.CONNECT, new ReducedMessage("null")));
        else
            c.setLobby(lobby);

        return true;
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

        c.send(new Answer<>(AnswerType.ERROR, DemandType.CREATE_GAME, new ReducedMessage("null")));
        return true;
    }

    private void loadLobby() {
        Game loadedGame = null;
        Lobby loadedLobby;

        try {
            File f = new File(BACKUPPATH);
            if (f.exists())
                loadedGame = GameMemory.load(BACKUPPATH);
        } catch (ParserConfigurationException | SAXException e) {
            LOGGER.log(Level.SEVERE, "Cannot load backup", e);
        }

        if (loadedGame != null) {
            loadedLobby = new Lobby(loadedGame);
            loadedLobby.setNumberOfPlayers(loadedGame.getNumPlayers());
            //lobbyList.add(loadedLobby);
            lobby = loadedLobby;
        }
    }
}
