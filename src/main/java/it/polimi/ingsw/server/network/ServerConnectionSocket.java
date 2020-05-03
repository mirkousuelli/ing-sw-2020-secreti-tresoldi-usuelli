package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.message.Lobby;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
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
    private static final String PATH = "src/main/java/it/polimi/ingsw/server/network/message/message";
    private static final String BACKUPPATH = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_test.xml";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private final Map<ServerClientHandler, String> waitingConnection = new HashMap<>();
    private final List<Lobby> lobbyList = new ArrayList<>();

    public ServerConnectionSocket(int port) {
        this.port = port;

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
                handler = new ServerClientHandlerSocket(socket, this, PATH + random.nextInt() + ".xml");
                executor.submit(handler);
            }
            catch(IOException e) {
                LOGGER.log(Level.SEVERE, "Got an IOException, serverSocket closed", e);
                break;//In case the serverSocket gets closed
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
    public synchronized void connect(ServerClientHandler c, String name) {

        for (Lobby l : lobbyList) {
            if (l.isReloaded() && l.getGame().getPlayer(name) != null) {
                l.addPlayer(name, c);
                c.setLobby(l);
                LOGGER.info("Reloaded!");
                return;
            }
        }

        waitingConnection.put(c, name);

        LOGGER.info(() -> name + " put!");
        c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.CONNECT, new ReduceDemandChoice("success")));
        LOGGER.info("Connect answer sent!");
    }

    @Override
    public synchronized boolean prelobby(ServerClientHandler c, Demand demand) throws ParserConfigurationException, SAXException {
        Lobby lobby;
        String value = ((ReduceDemandChoice) demand.getPayload()).getChoice();

        switch (value) {
            case "1":
                lobby = new Lobby();

                lobbyList.add(lobby);

                lobby.addPlayer(waitingConnection.get(c), c);
                lobby.setCurrentPlayer(lobby.getReducedPlayerList().get(0).getNickname());
                waitingConnection.remove(c);
                c.setLobby(lobby);
                c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.CREATE_GAME, new ReduceDemandChoice(lobby.getId())));
                LOGGER.info("Success create game sent!");
                return false;

            case "2":
                    c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.ASK_LOBBY, new ReduceDemandChoice("success")));
                    LOGGER.info("Success ask lobby sent!");
                return false;

            default:
                c.asyncSend(new Answer<>(AnswerType.ERROR, (DemandType) demand.getHeader(), new ReduceDemandChoice("Error")));
        }

        return true;
    }

    @Override
    public synchronized boolean lobby(ServerClientHandler c, Demand demand) {
        Lobby lobby;

        switch ((DemandType) demand.getHeader()) {
            case CREATE_GAME:
                lobby = c.getLobby();

                lobby.setNumberOfPlayers(Integer.parseInt(((ReduceDemandChoice) demand.getPayload()).getChoice()));
                c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.WAIT, new ReduceDemandChoice("success")));
                LOGGER.info("Success wait game sent!");
                return false;

            case ASK_LOBBY:
                boolean spaceInLobby;
                String lobbyString = ((ReduceDemandChoice) demand.getPayload()).getChoice();

                lobby = null;
                for (Lobby l : lobbyList) {
                    if (l.getId().equals(lobbyString)) {
                        lobby = l;
                    }
                }

                if (lobby != null) {
                    spaceInLobby = lobby.addPlayer(waitingConnection.get(c), c);

                    if (spaceInLobby) {
                        lobby.addPlayer(waitingConnection.get(c), c);
                        c.setLobby(lobby);
                        waitingConnection.remove(c);
                        c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.JOIN_GAME, lobby.getColor(c)));
                        LOGGER.info("Success join game sent!");
                        return false;
                    }
                    else {
                        c.asyncSend(new Answer<>(AnswerType.ERROR, DemandType.ASK_LOBBY, new ReduceDemandChoice("Error!")));
                        LOGGER.info("Error lobby full!");
                        return true;
                    }
                }

                c.asyncSend(new Answer<>(AnswerType.ERROR, DemandType.ASK_LOBBY, new ReduceDemandChoice("Error!")));
                LOGGER.info("Error lobby does not exists!");
                return true;

            default:
                c.asyncSend(new Answer<>(AnswerType.ERROR, (DemandType) demand.getHeader(), new ReduceDemandChoice("Error!")));
                LOGGER.info("Error lobby!");
        }

        return true;
    }

    private void loadLobby() {
        Game loadedGame = null;
        Lobby loadedLobby;

        try {
            loadedGame = GameMemory.load(BACKUPPATH);
        } catch (ParserConfigurationException | SAXException e) {
            LOGGER.log(Level.SEVERE, "Cannot load backup", e);
        }

        if (loadedGame != null) {
            loadedLobby = new Lobby(loadedGame);
            loadedLobby.setNumberOfPlayers(loadedGame.getNumPlayers());
            lobbyList.add(loadedLobby);
        }
    }
}
