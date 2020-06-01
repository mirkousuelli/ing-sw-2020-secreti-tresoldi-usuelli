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
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ServerConnectionSocket {
    private final int port;
    private static final String BACKUP_PATH = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_lobby.xml";
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private final Map<String, ServerClientHandler> waitingConnection = new HashMap<>();

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
            File f = new File(BACKUP_PATH);
            if (f.exists()) {
                loadedGame = GameMemory.load(BACKUP_PATH);
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

    private void createLobby(ServerClientHandler c) {
        try {
            lobby = new Lobby();
            c.setCreator(true);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot create a lobby!", e);
            c.closeSocket();
            isActive = false;
        }
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





    void deregisterConnection(ServerClientHandler c) {
        for (ServerClientHandler ch : lobby.getServerClientHandlerList()) {
            ch.asyncSend(new Answer<>(AnswerType.SUCCESS, new ReducedPlayer(lobby.getPlayer(c))));
        }

        c.setActive(false);
        lobby.deletePlayer(c);
    }

    void SuddenDisconnection() {
        for (ServerClientHandler ch : lobby.getServerClientHandlerList()) {
            ch.closeSocket();
        }

        waitingConnection.clear();
        File f = new File(Lobby.backupPath);
        if (f.exists())
            lobby.setReloaded(true);
        else
            lobby = null;
    }





    synchronized boolean connect(ServerClientHandler c, String name) {
        if (lobby != null) {
            if (lobby.isReloaded()) {
                if (!lobby.isPresentInGame(name)) {
                    if (waitingConnection.keySet().size() > 0) { //not present in reloaded lobby and someone is already waiting --> changeName or exit client-side
                        c.send(new Answer<>(AnswerType.ERROR));
                        return true; //toRepeat
                    }
                    else //not present in reloaded lobby but no waiting players, so create a new lobby
                        createLobby(c);
                }
            }
            else {
                if (waitingConnection.get(name) != null) { //userName already exists --> changeName or exit client-side
                    c.send(new Answer<>(AnswerType.ERROR));
                    return true; //toRepeat
                }

                if (waitingConnection.keySet().size() == lobby.getNumberOfPlayers()) { //lobby full --> exit server-side
                    c.closeSocket();
                    c.setLoggingOut(false);
                    return false; //not toRepeat because it has to stop
                }
            }
        }
        else //creator
            createLobby(c);

        waitingConnection.put(name, c); //creator or joiner

        if(canStart()) //add everyone to the game if the number of players is reached
            startMatch();

        if (!isLobbyReloaded())
            c.send(new Answer<>(AnswerType.SUCCESS, new ReducedPlayer(name, c.isCreator())));

        return false; //not toRepeat
    }

    private void startMatch() {
        AtomicInteger i = new AtomicInteger();

        waitingConnection.values().stream()
                .sorted(Comparator.comparing(ServerClientHandler::isCreator, Comparator.reverseOrder()))
                .collect(Collectors.toList())
                .forEach( c -> {
                    if (i.get() <= lobby.getNumberOfPlayers()) {
                        lobby.addPlayer(c.getName(), c);
                        i.getAndIncrement();
                    } else
                        c.closeSocket();

                    if (c.isCreator())
                        lobby.setCurrentPlayer(c.getName());
                });

        synchronized (this) {
            notifyAll();
        }
    }

    boolean canStart() {
        int waitingConnectionSize;
        int numOfPl;

        synchronized (waitingConnection) {
            waitingConnectionSize = waitingConnection.keySet().size();
        }

        synchronized (lobby.lockLobby) {
            numOfPl = lobby.getNumberOfPlayers();
        }

        if (numOfPl == -1) return false;

        return waitingConnectionSize == numOfPl;
    }

    synchronized boolean numOfPlayers(ServerClientHandler c, Demand demand) {
        String value = ((ReducedMessage) demand.getPayload()).getMessage();
        int numOfPls = Integer.parseInt(value);

        if (demand.getHeader() == DemandType.CREATE_GAME) {
            if (numOfPls == 2 || numOfPls == 3) {
                lobby.setNumberOfPlayers(numOfPls);
                if(canStart()) //add everyone to the game if the number of players is reached
                    startMatch();
                return false;
            }
        }

        c.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    synchronized boolean newGame(ServerClientHandler c, Demand demand) {
        String response = ((ReducedMessage) demand.getPayload()).getMessage();

        if (response.equals("n")) {
            if (c.isCreator() && waitingConnection.keySet().size() > 1)
                lobby.setNewCreator(c).setCreator(true);

            lobby.deletePlayer(c);
            c.setLoggingOut(true);
            c.closeSocket();
            return false;
        }
        else if (response.equals("y")) {
            waitingConnection.put(c.getName(), c);

            if (canStart()) {
                lobby.getGame().setState(State.START);
                lobby.cleanGame();
                startMatch();
            }
            return false;
        }

        c.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    boolean isLobbyReloaded() {
        boolean isReloaded;

        synchronized (lobby.lockLobby) {
            isReloaded = lobby.isReloaded();
        }

        return isReloaded;
    }

    Lobby getLobby() {
        Lobby toReturn;

        synchronized (this) {
            toReturn = lobby;
        }

        return toReturn;
    }
}
