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

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Class which manages the server side connection protocol through sockets
 */
public class ServerConnectionSocket {
    private final int port;
    private static final String BACKUP_PATH = Lobby.backupPath;
    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    private final Map<String, ServerClientHandler> waitingConnection = new HashMap<>();
    private final Map<String, ServerClientHandler> waitingConnectionFromReload = new HashMap<>();

    private Lobby lobby;
    private boolean isActive;
    private boolean alreadyNewGame;

    /**
     * Constructor which initialize the server socket through its port
     *
     * @param port server socket port
     */
    public ServerConnectionSocket(int port) {
        this.port = port;

        lobby = null;
        loadLobby();

        isActive = false;
        alreadyNewGame = false;
    }

    /**
     * Method that starts server socket connection stream and manages its behaviours until closing part
     */
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

    /**
     * Method that load a previous lobby in order to recover a past match saved
     */
    private void loadLobby() {
        Game loadedGame = null;

        try {
            File f = new File(BACKUP_PATH);
            if (f.exists()) {
                loadedGame = GameMemory.load(BACKUP_PATH);
                if (loadedGame.getState().getName().equals(State.VICTORY.toString()))
                    loadedGame = null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot load backup", e);
        }

        if (loadedGame != null) {
            lobby = new Lobby(loadedGame);
            lobby.setNumberOfPlayers(loadedGame.getNumPlayers());
        }
    }

    /**
     * Method that creates a new lobby by adding the first connection as the lobby creator
     *
     * @param c first player connection as creator
     */
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

    /**
     * Method which manages a sudden disconnection case in order to correctly save the game in that scenario
     */
    void SuddenDisconnection() {
        if (waitingConnection.size() != lobby.getGame().getNumPlayers()) {
            List<String> names = waitingConnection.keySet().stream()
                    .filter(name -> lobby.isPresentInGame(name))
                    .collect(Collectors.toList());

            waitingConnection.keySet().forEach(name -> {
                if(!names.contains(name)) {
                    lobby.deletePlayer(waitingConnection.get(name));
                    lobby.setNumberOfPlayers(lobby.getNumberOfPlayers() - 1);
                }
            });

            waitingConnection.keySet().removeIf(name -> !names.contains(name));
            return;
        }

        for (ServerClientHandler ch : lobby.getServerClientHandlerList())
            ch.closeSocket();

        waitingConnection.clear();
        File f = new File(BACKUP_PATH);
        if (f.exists())
            lobby.setReloaded(true);
        else
            lobby = null;
    }

    /**
     * Method that operates the proper connection
     *
     * @param c player's connection
     * @param name player's nickname
     * @return {@code true} connected successfully, {@code false} connection gone wrong
     */
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

    /**
     * Method that makes the match starting
     */
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

    /**
     * Method that approve the intention to start the game checking all the condition
     *
     * @return {@code true} the game can start, {@code false} the game cannot start
     */
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

    /**
     * Method that sets the game players dimension by processing the demand
     *
     * @param c creator's connection
     * @param demand demand message received containing number of players information
     * @return {@code true} something went wrong, {@code false} match started
     */
    synchronized boolean numOfPlayers(ServerClientHandler c, Demand demand) {
        String value = ((ReducedMessage) demand.getPayload()).getMessage();
        int numOfPls = Integer.parseInt(value);

        if (demand.getHeader() == DemandType.CREATE_GAME) {
            if (numOfPls == 2 || numOfPls == 3) {
                if (!lobby.isReloaded() || numOfPls == lobby.getGame().getNumPlayers()) {
                    lobby.setNumberOfPlayers(numOfPls);
                    if (canStart()) //add everyone to the game if the number of players is reached
                        startMatch();
                    return false;
                }
            }
        }

        c.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    /**
     * Method that adds the worker to the list of workers of the player
     *
     * @param c creator's connection
     * @return {@code true} new game started, {@code false} creator logged out
     */
    synchronized boolean newGame(ServerClientHandler c, Demand demand) {
        String response = ((ReducedMessage) demand.getPayload()).getMessage();

        if (response.equals("n")) {
            c.setLoggingOut(true);
            c.closeSocket();
            return false;
        }
        else if (response.equals("y")) {
            if (!alreadyNewGame) {
                waitingConnection.clear();
                waitingConnectionFromReload.clear();
                lobby.clean();
                lobby = null;
                File file = new File(BACKUP_PATH);
                boolean b = file.delete();
                alreadyNewGame = true;
            }

            c.setCreator(false);
            waitingConnectionFromReload.put(c.getName(), c);
            return false;
        }

        c.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    /**
     * Method that says if the lobby has been reloaded from a previous storage
     */
    boolean isLobbyReloaded() {
        boolean isReloaded;

        synchronized (lobby.lockLobby) {
            isReloaded = lobby.isReloaded();
        }

        return isReloaded;
    }

    /**
     * Method that gets the current lobby
     *
     * @return {@code Lobby} current lobby
     */
    Lobby getLobby() {
        Lobby toReturn;

        synchronized (this) {
            toReturn = lobby;
        }

        return toReturn;
    }

    /**
     * Method that check if the connection is waiting the reloading
     *
     * @param c player's connection
     * @return {@code true} waiting for reload, {@code false} not waiting for reload
     */
    boolean isInWaitingConnectionFromReload(ServerClientHandler c) {
        boolean toReturn;

        synchronized (waitingConnectionFromReload) {
            toReturn = waitingConnectionFromReload.get(c.getName()) != null;
        }

        return toReturn;
    }

    void deletePlayer(ServerClientHandler c) {
        synchronized (waitingConnection) {
            waitingConnection.remove(c.getName());
        }

        synchronized (waitingConnectionFromReload) {
            waitingConnectionFromReload.remove(c.getName());
        }

        lobby = null;
        loadLobby();
    }
}
