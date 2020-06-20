package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.storage.GameMemory;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    private final Map<String, ServerClientHandlerSocket> waitingConnection = new HashMap<>();
    private final Map<String, ServerClientHandlerSocket> waitingConnectionFromReload = new HashMap<>();

    private Lobby lobby;
    private boolean isActive;
    private boolean alreadyNewGame;

    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

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
    public void startServer() {
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ServerClientHandlerSocket handler;
        ExecutorService executor = Executors.newCachedThreadPool();
        Socket socket = null;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server ready");

            isActive = true;
            while (isActive) {
                try {
                    socket = serverSocket.accept();
                    handler = new ServerClientHandlerSocket(socket, this);
                    executor.submit(handler);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Got an Exception, serverSocket closed", e);
                    isActive = false; //In case the serverSocket gets closed
                }
            }

            executor.shutdown();
            if (socket != null) socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, port not available", e); //port not available
        }
    }

    /**
     * Method that load a previous lobby in order to recover a past match saved
     */
    private void loadLobby() {
        Game loadedGame = null;

        try {
            File f = new File(Lobby.BACKUP_PATH);
            if (f.exists()) {
                loadedGame = GameMemory.load(Lobby.BACKUP_PATH);
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
    private void createLobby(ServerClientHandlerSocket c) {
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
    void suddenDisconnection(ServerClientHandlerSocket c) {
        deletePlayer(c);

        if (waitingConnection.isEmpty()) return;

        if (waitingConnection.size() + 1 > lobby.getNumberOfPlayers() && lobby.getNumberOfPlayers() > 0) { //a player (c) has been defeated but the are other players remaining (more than one)
            lobby.deletePlayer(c.getName());
            lobby.setNumberOfPlayers(lobby.getNumberOfPlayers() - 1);
        } else if (lobby.getNumberOfPlayers() == -1) { //creator disconnects before selecting the number of players
            if (c.isCreator()) {
                ServerClientHandlerSocket newCreator = new ArrayList<>(waitingConnection.values()).get(0);

                newCreator.setCreator(true);
                newCreator.setIsToRestart(true);
                newCreator.send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.PLAYER));
                waitRestart(newCreator);
                newCreator.setOkToRestart(false);
                newCreator.callWatchDog(true);
            }
        } else {
            for (ServerClientHandlerSocket serverClientHandler : waitingConnection.values()) { //there was an unexpected disconnection, stop the match for all the players in game
                serverClientHandler.send(new Answer<>(AnswerType.CLOSE));
                waitRestart(serverClientHandler);
                serverClientHandler.callWatchDog(true); //restart the game for the remaining ones
            }

            //load lobby if there is one to load
            File f = new File(Lobby.BACKUP_PATH);
            if (f.exists())
                loadLobby();
            else
                lobby = null;
        }
    }

    /**
     * Method that operates the proper connection
     *
     * @param c    player's connection
     * @param name player's nickname
     * @return {@code true} connected successfully, {@code false} connection gone wrong
     */
    synchronized boolean connect(ServerClientHandlerSocket c, String name) {
        if (lobby != null) {
            if (lobby.isReloaded()) {
                if (connectReload(c, name))
                    return true; //toRepeat
            } else {
                Boolean toRepeat = connectBasic(c, name); //if toRepeat is null there is still something to do
                if (toRepeat != null)
                    return toRepeat;
            }
        } else //creator
            createLobby(c);

        waitingConnection.put(name, c); //creator or joiner

        if (canStart()) //add everyone to the game if the number of players is reached
            startMatch();

        if (!isLobbyReloaded())
            c.send(new Answer<>(AnswerType.SUCCESS, new ReducedPlayer(name, c.isCreator())));

        return false; //not toRepeat
    }

    private synchronized Boolean connectBasic(ServerClientHandlerSocket c, String name) {
        Boolean toRepeat = null;

        if (waitingConnection.get(name) != null) { //userName already exists --> changeName or exit client-side
            c.send(new Answer<>(AnswerType.ERROR));
            toRepeat = true; //toRepeat
        } else if (waitingConnection.keySet().size() == lobby.getNumberOfPlayers()) { //lobby full --> exit server-side
            c.closeSocket();
            c.setLoggingOut(false);
            toRepeat = false; //not toRepeat because it has to stop
        }

        return toRepeat;
    }

    private synchronized boolean connectReload(ServerClientHandlerSocket c, String name) {
        if (!lobby.isPresentInGame(name)) {
            if (!waitingConnection.keySet().isEmpty()) { //not present in reloaded lobby and someone is already waiting --> changeName or exit client-side
                c.send(new Answer<>(AnswerType.ERROR));
                return true; //toRepeat
            } else //not present in reloaded lobby but no waiting players, so create a new lobby
                createLobby(c);
        }

        return false;
    }

    /**
     * Method that makes the match starting
     */
    private void startMatch() {
        AtomicInteger i = new AtomicInteger();

        waitingConnection.values().stream()
                .sorted(Comparator.comparing(ServerClientHandler::isCreator, Comparator.reverseOrder()))
                .collect(Collectors.toList())
                .forEach(c -> {
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
     * @param c      creator's connection
     * @param demand demand message received containing number of players information
     * @return {@code true} something went wrong, {@code false} match started
     */
    synchronized boolean numOfPlayers(ServerClientHandlerSocket c, Demand demand) {
        if (demand == null) return false;

        String value = ((ReducedMessage) demand.getPayload()).getMessage();
        int numOfPls = Integer.parseInt(value);

        if (demand.getHeader() == DemandType.CREATE_GAME && (numOfPls == 2 || numOfPls == 3)) {
            if (!lobby.isReloaded() || numOfPls == lobby.getGame().getNumPlayers()) {
                lobby.setNumberOfPlayers(numOfPls);
                if (canStart()) //add everyone to the game if the number of players is reached
                    startMatch();
                return false;
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
    synchronized boolean newGame(ServerClientHandlerSocket c, Demand demand) {
        if (demand == null) return false;

        String response = ((ReducedMessage) demand.getPayload()).getMessage();

        if (response.equals("n")) {
            c.setLoggingOut(true);
            c.closeSocket();
            return false;
        } else if (response.equals("y")) {
            if (!alreadyNewGame) {
                waitingConnection.clear();
                waitingConnectionFromReload.clear();
                lobby.clean();
                lobby = null;
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
    boolean isInWaitingConnectionFromReload(ServerClientHandlerSocket c) {
        boolean toReturn;

        synchronized (waitingConnectionFromReload) {
            toReturn = waitingConnectionFromReload.get(c.getName()) != null;
        }

        return toReturn;
    }

    void deletePlayer(ServerClientHandlerSocket c) {
        synchronized (waitingConnection) {
            waitingConnection.remove(c.getName());
        }

        synchronized (waitingConnectionFromReload) {
            waitingConnectionFromReload.remove(c.getName());
        }
    }

    private void waitRestart(ServerClientHandlerSocket newCreator) {
        synchronized (newCreator.lockRestart) {
            while (!newCreator.isOkToRestart()) {
                try {
                    newCreator.lockRestart.wait();
                } catch (InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "Got an unexpected InterruptedException", e);
                }
            }
        }
    }
}
