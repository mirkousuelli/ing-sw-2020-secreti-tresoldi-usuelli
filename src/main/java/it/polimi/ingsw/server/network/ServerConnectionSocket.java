package it.polimi.ingsw.server.network;

import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.network.message.Lobby;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerConnectionSocket implements ServerConnection {
    private final int port;
    private static final String FILEXML = "src/main/java/it/polimi/ingsw/server/network/message/message";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private final Map<ServerClientHandler, String> waitingConnection = new HashMap<>();
    private final List<Lobby> lobbyList = new ArrayList<>();

    public ServerConnectionSocket(int port) {
        this.port = port;
    }

    public void startServer() throws IOException {
        //It creates threads when necessary, otherwise it re-uses existing one when possible
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

        while (true) {
            try {
                socket = serverSocket.accept();
                ServerClientHandlerSocket handler = new ServerClientHandlerSocket(socket, this, FILEXML + waitingConnection.size() + ".xml");
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
        //TODO
    }

    //Wait for another player
    @Override
    public void preLobby(ServerClientHandler c, String name) {
        synchronized (waitingConnection) {
            waitingConnection.put(c, name);
        }

        LOGGER.info(() -> name + " put!");
        c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.CONNECT, ""));
        LOGGER.info("Connect answer sent!");

    }

    @Override
    public void lobby(Demand demand, ServerClientHandler c) throws ParserConfigurationException, SAXException {
        Lobby lobby = null;
        String value =  demand.getPayload().toString();
        DemandType demandType = (DemandType) demand.getHeader();

        synchronized (lobbyList) {
            for (Lobby l : lobbyList) {
                if (l.getID().equals(value)) {
                    lobby = l;
                    break;
                }
            }
        }

        switch (demandType) {
            case CREATE_GAME:
                if (lobby == null) {
                    lobby = new Lobby(Integer.parseInt(value));

                    synchronized (lobbyList) {
                        lobbyList.add(lobby);
                    }
                    c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.CREATE_GAME, lobby.getID()));
                    LOGGER.info("Success create game sent!");
                }
                else {
                    c.asyncSend(new Answer<>(AnswerType.ERROR, DemandType.CREATE_GAME, "Not a valid create game"));
                    LOGGER.info("Error create game sent!");
                }
                break;
            case JOIN_GAME:
                if (lobby != null) {
                    synchronized (waitingConnection) {
                        lobby.addPlayer(waitingConnection.get(c), c);
                    }
                    c.asyncSend(new Answer<>(AnswerType.SUCCESS, DemandType.JOIN_GAME, ""));
                    LOGGER.info("Success join game sent!");
                }
                else {
                    c.asyncSend(new Answer<>(AnswerType.ERROR, DemandType.JOIN_GAME, "Not a join game"));
                    LOGGER.info("Error join game sent!");
                }
                break;
            default:
                throw new NotAValidInputRunTimeException("Not a valid demand type");
        }
    }
}
