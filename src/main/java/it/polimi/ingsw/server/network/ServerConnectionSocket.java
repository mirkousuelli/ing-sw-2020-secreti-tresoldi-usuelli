package it.polimi.ingsw.server.network;

import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.game.Game;
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
    private final String FILEXML = "src/main/java/it/polimi/ingsw/server/network/message/message" + this.toString() + ".xml"; // X TESTING
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private final Map<String, ServerClientHandler> waitingConnection = new HashMap<>();
    private final Map<ServerClientHandler, ServerClientHandler> playingConnection = new HashMap<>();

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
                ServerClientHandlerSocket handler = new ServerClientHandlerSocket(socket, this, FILEXML);
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
    public synchronized void deregisterConnection(ServerClientHandler c) {
        ServerClientHandler opponent = playingConnection.get(c);
        if(opponent != null) {
            opponent.closeConnection();
        }

        playingConnection.remove(c);
        playingConnection.remove(opponent);
        waitingConnection.keySet().removeIf(s -> waitingConnection.get(s) == c);
    }

    //Wait for another player
    @Override
    public synchronized void lobby(ServerClientHandler c, String name) throws ParserConfigurationException, SAXException {
        waitingConnection.put(name, c);
        LOGGER.info(() -> name + " put!");
        if (waitingConnection.size() == 2) {
            List<String> keys = new ArrayList<>(waitingConnection.keySet());
            ServerClientHandler c1 = waitingConnection.get(keys.get(0));
            ServerClientHandler c2 = waitingConnection.get(keys.get(1));
            View player1View = new RemoteView(keys.get(0), c1);
            View player2View = new RemoteView(keys.get(1), c2);
            Game model = new Game();
            Controller controller = new Controller(model);
            //model.addObserver(player1View);
            //model.addObserver(player2View);
            player1View.addObserver(controller);
            player2View.addObserver(controller);
            playingConnection.put(c1, c2);
            playingConnection.put(c2, c1);
            waitingConnection.clear();

            //TODO
            /*c1.asyncSend(model.getBoardCopy());
            c2.asyncSend(model.getBoardCopy());
            if(model.isPlayerTurn(player1)){
                c1.asyncSend(gameMessage.moveMessage);
                c2.asyncSend(gameMessage.waitMessage);
            } else {
                c2.asyncSend(gameMessage.moveMessage);
                c1.asyncSend(gameMessage.waitMessage);
            }*/


        }
    }
}
