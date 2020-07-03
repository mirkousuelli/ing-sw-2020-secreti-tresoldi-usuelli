package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.storage.GameMemory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class which manages the server side connection protocol through sockets
 */
public class ServerConnectionSocket {

    private final int port;

    private final Map<String, ServerClientHandlerSocket> connectedPlayers = new HashMap<>();
    private final Map<String, ServerClientHandlerSocket> pendingPlayers = new HashMap<>();

    private Lobby lobby;
    private boolean isActive;
    private boolean alreadyNewGame;

    private final Map<Integer, Lobby> loadedLobbyMap = new HashMap<>();
    private final Map<Integer, Integer> loadedLobbyPathMap = new HashMap<>();
    private int numOfLobbies;

    private static final String LOBBY_DIR = "backups";
    private static final String LOBBY_NAME = LOBBY_DIR + "/backup";
    private static final String EXTENSION = ".xml";

    private static final Logger LOGGER = Logger.getLogger(ServerConnectionSocket.class.getName());

    /**
     * Constructor which initializes the server socket through its port
     *
     * @param port server socket port
     */
    public ServerConnectionSocket(int port) {
        this.port = port;

        lobby = null;
        numOfLobbies = 0;
        loadLobbies();
        moveAndLoadBackUpLobby();

        isActive = false;
        alreadyNewGame = false;
    }

    /**
     * Method that starts server socket connection stream and manages its behaviour until closing part
     */
    public void startServer() {
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ServerClientHandlerSocket handler;
        ExecutorService executor = Executors.newCachedThreadPool();
        Socket socket;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Server ready");

            isActive = true;
            do {
                socket = serverSocket.accept();
                handler = new ServerClientHandlerSocket(socket, this);
                executor.submit(handler);
            } while (isActive);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, port not available", e); //port not available
            isActive = false;
        } finally {
            executor.shutdown();
        }
    }

    /*-------------------------------------------------GETTER---------------------------------------------------------*/
    private synchronized int getLobbyPlayerListHashCode(Lobby lobby) {
        return lobby.getGame().getPlayerList().stream()
                .map(Player::getNickName)
                .collect(Collectors.toList())
                .hashCode();
    }

    Lobby getLobby() {
        Lobby toReturn;

        synchronized (this) {
            toReturn = lobby;
        }

        return toReturn;
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*-------------------------------------------------PREDICATE------------------------------------------------------*/
    private synchronized Lobby isPlayerInALobby(String name) {
        return loadedLobbyMap.values().stream()
                .filter(l -> l.isPresentInGame(name))
                .reduce(null, (lobby1, lobby2) -> lobby1 != null
                        ? lobby1
                        : lobby2
                );
    }

    /**
     * Method that says if the lobby has been reloaded from a previous storage
     */
    boolean isLobbyReloaded() {
        if (lobby == null) return false;

        boolean isReloaded;

        synchronized (lobby.lockLobby) {
            isReloaded = lobby.isReloaded();
        }

        return isReloaded;
    }

    boolean isInPendingConnection(ServerClientHandlerSocket player) {
        return pendingPlayers.containsValue(player);
    }
    /*----------------------------------------------------------------------------------------------------------------*/


    /*-------------------------------------------------LOBBY----------------------------------------------------------*/
    private synchronized void loadLobbies() {
        int numOfLobby;

        loadedLobbyMap.clear();
        loadedLobbyPathMap.clear();

        if (!Files.exists(Paths.get(LOBBY_DIR))) return;

        try (Stream<Path> files = Files.list(Paths.get(LOBBY_DIR))) {
            numOfLobby = (int) files.count();
        } catch (Exception e) {
            LOGGER.info("Got an IOException, couldn't count lobbies");
            return;
        }

        numOfLobbies = numOfLobby;
        Lobby lobbyLoaded;
        int lobbyHashCode;
        int i = 1;
        while (i <= numOfLobbies) {
            lobbyLoaded = loadLobby(i);
            if (lobbyLoaded != null) {
                lobbyHashCode = getLobbyPlayerListHashCode(lobbyLoaded);
                loadedLobbyMap.put(lobbyHashCode, lobbyLoaded);
                loadedLobbyPathMap.put(lobbyHashCode, i);
                i++;
            } else
                removeAndShiftLobbies(i);
        }
    }

    /**
     * Method that load all the previous lobbies in order to recover a past match saved
     */
    private synchronized Lobby loadLobby(int i) {
        return loadLobby(LOBBY_NAME + i + EXTENSION);
    }

    private synchronized Lobby loadLobby(String path) {
        Game loadedGame = null;
        Lobby loadedLobby = null;

        try {
            if (Files.exists(Paths.get(path))) {
                loadedGame = GameMemory.load(path);
                if (loadedGame != null && loadedGame.getState().getName().equals(State.VICTORY.toString())) {
                    loadedGame = null;
                    if (path.equals(Lobby.BACKUP_PATH))
                        Files.deleteIfExists(Paths.get(Lobby.BACKUP_PATH));
                    else
                        removeAndShiftLobbies(path.charAt(14));
                }
            }
        } catch (Exception e) {
            LOGGER.info(() -> "Cannot load " + path);
            return null;
        }

        if (loadedGame != null) {
            loadedLobby = new Lobby(loadedGame);
            loadedLobby.setNumberOfPlayers(loadedGame.getNumPlayers());
            loadedLobby.setReloaded(true);
        }

        return loadedLobby;
    }

    private synchronized void moveAndLoadBackUpLobby() {
        int lobbyHashCode;
        int index;
        Lobby lobbyToSave;

        if (lobby != null) {
            State gameState = State.parseString(lobby.getGame().getState().getName());
            if (!Files.exists(Paths.get(Lobby.BACKUP_PATH)) && gameState != null && gameState.ordinal() >= State.CHOOSE_WORKER.ordinal())
                GameMemory.save(lobby.getGame(), Lobby.BACKUP_PATH);
            lobby.setReloaded(true);
            lobby = null;
        }

        if (!Files.exists(Paths.get(Lobby.BACKUP_PATH))) return;

        if (lobby != null)
            lobby.clean();

        lobbyToSave = loadLobby(Lobby.BACKUP_PATH);
        if (lobbyToSave == null) return;

        lobbyHashCode = getLobbyPlayerListHashCode(lobbyToSave);

        if (loadedLobbyMap.get(lobbyHashCode) != null) {
            index = loadedLobbyPathMap.get(lobbyHashCode);
            loadedLobbyMap.put(lobbyHashCode, lobbyToSave);
        } else {
            numOfLobbies++;
            loadedLobbyMap.put(lobbyHashCode, lobbyToSave);
            loadedLobbyPathMap.put(lobbyHashCode, numOfLobbies);
            index = numOfLobbies;
        }

        updateBackup(index);
    }

    private synchronized void updateBackup(int index) {
        try {
            if (!Files.exists(Paths.get(Lobby.BACKUP_PATH))) return;

            if (!Files.exists(Paths.get(LOBBY_DIR)))
                Files.createDirectory(Paths.get(LOBBY_DIR));

            if (!Files.exists(Paths.get(LOBBY_NAME + index + EXTENSION)))
                Files.createFile(Paths.get(LOBBY_NAME + index + EXTENSION));

            Files.move(Paths.get(Lobby.BACKUP_PATH), Paths.get(LOBBY_NAME + index + EXTENSION), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, couldn't move file", e);
        }
    }

    private synchronized void updateLobbyHashCode() {
        int newLobbyHashCode = getLobbyPlayerListHashCode(lobby);
        int oldLobbyHashCode = lobby.getReducedPlayerList().stream()
                .map(ReducedPlayer::getNickname)
                .collect(Collectors.toList())
                .hashCode();

        loadedLobbyMap.put(newLobbyHashCode, lobby);
        loadedLobbyPathMap.put(newLobbyHashCode, loadedLobbyPathMap.get(oldLobbyHashCode));

        loadedLobbyMap.remove(oldLobbyHashCode);
        loadedLobbyPathMap.remove(oldLobbyHashCode);
    }

    private synchronized void removeAndShiftLobbies(int indexToRemove) {
        int i = indexToRemove + 1;
        try {
            while (Files.exists(Paths.get(LOBBY_NAME + i + EXTENSION))) {
                Files.move(Paths.get(LOBBY_NAME + i + EXTENSION), Paths.get(LOBBY_NAME + (i - 1) + EXTENSION), StandardCopyOption.REPLACE_EXISTING);
                i++;
            }
            Files.deleteIfExists(Paths.get(LOBBY_NAME + (i - 1) + EXTENSION));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, couldn't delete a lobby");
        }

        if (numOfLobbies > 0)
            numOfLobbies--;
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-------------------------------------------------MATCH----------------------------------------------------------*/

    /**
     * Method that starts the game
     */
    private synchronized void startMatch() {
        AtomicInteger i = new AtomicInteger();
        alreadyNewGame = false;

        connectedPlayers.values().stream()
                .sorted(Comparator.comparing(ServerClientHandlerSocket::isCreator, Comparator.reverseOrder()))
                .collect(Collectors.toList())
                .forEach(player -> {
                    if (i.get() <= lobby.getNumberOfPlayers()) {
                        lobby.addPlayer(player);
                        i.getAndIncrement();
                    } else
                        player.closeSocket();

                    if (player.isCreator())
                        lobby.setCurrentPlayer(player.getName());
                });

        if (!lobby.isReloaded())
            numOfLobbies++;

        int lobbyHashCode = getLobbyPlayerListHashCode(lobby);
        loadedLobbyMap.put(lobbyHashCode, lobby);
        loadedLobbyPathMap.put(lobbyHashCode, numOfLobbies);

        synchronized (this) {
            notifyAll();
        }
    }

    /**
     * Method that approves the intention to start the game checking all the conditions
     *
     * @return {@code true} if the game can start, {@code false} otherwise
     */
    synchronized boolean canStart() {
        int waitingConnectionSize;
        int numOfPl;

        synchronized (connectedPlayers) {
            waitingConnectionSize = connectedPlayers.keySet().size();
        }

        synchronized (lobby.lockLobby) {
            numOfPl = lobby.getNumberOfPlayers();
        }

        if (numOfPl == -1) return false;

        return waitingConnectionSize >= numOfPl;
    }

    /**
     * Method which manages a sudden disconnection case in order to correctly save the game in that scenario
     */
    synchronized void suddenDisconnection(ServerClientHandlerSocket disconnectedPlayer) {
        if (closeIfItIsTooLateToPlayAgain(disconnectedPlayer)) return;
        if (connectedPlayers.size() <= 1) return; //safety check

        if (lobby.getNumberOfPlayers() > 0 && connectedPlayers.size() > lobby.getGame().getNumPlayers()) { //a player (disconnectedPlayer) has been defeated but the are other players remaining (more than one)
            updateLobbyHashCode();
            deletePlayer(disconnectedPlayer); //delete the disconnected player
            lobby.setNumberOfPlayers(lobby.getNumberOfPlayers() - 1);
        } else if (lobby.getNumberOfPlayers() == -1) { //creator disconnects before selecting the number of players (only new games not reloaded ones)
            deletePlayer(disconnectedPlayer); //delete the disconnected player
            if (disconnectedPlayer.isCreator()) {
                ServerClientHandlerSocket newCreator = new ArrayList<>(connectedPlayers.values()).get(0);

                deletePlayer(newCreator);
                newCreator.callWatchDog(true); //restart the game for the new creator (other possible players in wait can remain in wait)
            }
        } else {
            if (alreadyNewGame) return;
            connectedPlayers.values().forEach(p -> lobby.deletePlayerConnection(p));
            moveAndLoadBackUpLobby(); //load lobbies if there at least one to load
            connectedPlayers.remove(disconnectedPlayer.getName()); //delete the disconnected player

            List<ServerClientHandlerSocket> playersToAlert = connectedPlayers.values().stream()
                    .filter(serverClientHandlerSocket -> !pendingPlayers.containsValue(serverClientHandlerSocket))
                    .collect(Collectors.toList());

            for (ServerClientHandlerSocket serverClientHandler : playersToAlert) { //there was an unexpected disconnection, stop the match for all the players in game
                serverClientHandler.setIsToRestart(true);
                serverClientHandler.send(new Answer<>(AnswerType.CLOSE));
                waitRestart(serverClientHandler);
                serverClientHandler.setOkToRestart(false);
                serverClientHandler.callWatchDog(true); //restart the game for the remaining ones
            }

            //reset
            pendingPlayers.putAll(connectedPlayers);
            connectedPlayers.clear();
        }
    }

    synchronized void deletePlayer(ServerClientHandlerSocket player) {
        synchronized (connectedPlayers) {
            connectedPlayers.remove(player.getName());
            lobby.deletePlayer(player);
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-------------------------------------------------CONNECT--------------------------------------------------------*/

    /**
     * Method that operates the proper connection
     *
     * @param player player's connection
     * @param name   player's nickname
     * @return {@code true} connected successfully, {@code false} connection gone wrong
     */
    synchronized boolean connect(ServerClientHandlerSocket player, String name) {
        if (closeIfItIsTooLateToPlayAgain(player)) {
            pendingPlayers.remove(name);
            return false; //if it is too late it has to stop so it must not be repeated --> exit server-side
        }

        if (lobby != null && lobby.getGame().getState().getName().equals("victory") && !connectedPlayers.isEmpty()) {
            resetLobby();
            alreadyNewGame = false;
        }

        pendingPlayers.remove(name, player);

        Lobby otherLobby = isPlayerInALobby(name);
        if (lobby != null && otherLobby != null && !lobby.isReloaded() && !lobby.equals(otherLobby)) {
            player.send(new Answer<>(AnswerType.ERROR));
            return true; //toRepeat, 'player' is already in a lobby which is not the current one --> change name or exit client-side
        }

        if (lobby != null && connectedPlayers.values().stream().noneMatch(ServerClientHandlerSocket::isCreator))
            player.setCreator(true);

        if (!loadedLobbyMap.isEmpty() || lobby != null) { //if some lobbies where loaded, maybe 'player' is in one of them
            if (lobby != null) { //if 'player' isn't the first one to connect (which means another player loaded a lobby or created a new one)
                if (lobby.isReloaded()) { //if lobby is reloaded
                    Boolean toRepeat = connectReload(player, name); //if toRepeat is null there is still something to do...
                    if (toRepeat != null)
                        return toRepeat;
                } else { //else it is a new lobby
                    Boolean toRepeat = connectBasic(player, name); //if toRepeat is null there is still something to do...
                    if (toRepeat != null)
                        return toRepeat;
                }
            } else { //'player' is the first one to connect
                Lobby lobbyLoaded = isPlayerInALobby(name); //search a lobby with player
                if (lobbyLoaded != null) //if 'player' is in a lobby
                    lobby = lobbyLoaded; //then that lobby will be the one that will be used for this match
                else
                    createLobby(player); //else create a new lobby
            }
        } else //creator (no lobbies to load)
            createLobby(player);

        connectedPlayers.put(name, player); //creator or joiner

        if (canStart()) //add everyone to the game if the number of players chosen by the creator is reached
            startMatch();

        if (!lobby.isReloaded())
            player.send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.PLAYER, new ReducedPlayer(name, player.isCreator()))); //connection successful
        else if (!Files.exists(Paths.get(Lobby.BACKUP_PATH)) &&
                (lobby.getGame().getState().getName().equals("move") ||
                        lobby.getGame().getState().getName().equals("build") ||
                        lobby.getGame().getState().getName().equals("askAdditionalPower") ||
                        lobby.getGame().getState().getName().equals("additionalPower")))
            GameMemory.save(lobby.getGame(), Lobby.BACKUP_PATH);

        return false; //not toRepeat
    }

    private synchronized Boolean connectBasic(ServerClientHandlerSocket player, String name) {
        Boolean toRepeat = null;

        if (connectedPlayers.containsKey(name)) { //userName already exists --> changeName or exit client-side
            player.send(new Answer<>(AnswerType.ERROR));
            toRepeat = true; //toRepeat
        } else if (connectedPlayers.keySet().size() == lobby.getNumberOfPlayers()) { //lobby full --> exit server-side
            player.closeSocket();
            player.setLoggingOut(true);
            toRepeat = false; //not toRepeat because it has to stop
        }

        return toRepeat;
    }

    private synchronized Boolean connectReload(ServerClientHandlerSocket player, String name) {
        if (!lobby.isPresentInGame(name)) {
            if (!connectedPlayers.isEmpty()) { //not present in reloaded lobby and someone in already waiting --> changeName or exit client-side
                player.send(new Answer<>(AnswerType.ERROR));
                return true; //toRepeat
            } else //not present in reloaded lobby but no waiting players, so create a new lobby!
                createLobby(player);
        }

        return connectBasic(player, name);
    }

    /**
     * Method that sets the game players dimension by processing the demand
     *
     * @param player creator's connection
     * @param demand demand message received containing information about the number of players
     * @return {@code true} if something went wrong, {@code false} if the match started
     */
    synchronized boolean numOfPlayers(ServerClientHandlerSocket player, Demand demand) {
        if (demand == null) return false;

        String value = ((ReducedMessage) demand.getPayload()).getMessage();
        int numOfPls;
        try {
            numOfPls = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return true; //toRepeat
        }

        if (demand.getHeader() == DemandType.CREATE_GAME &&
                (numOfPls == 2 || numOfPls == 3) &&
                (!lobby.isReloaded() || numOfPls == lobby.getGame().getNumPlayers())) {
            lobby.setNumberOfPlayers(numOfPls);
            if (canStart()) //add everyone to the game if the number of players is reached
                startMatch();
            return false;
        }

        player.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    /**
     * Method that creates a new lobby by adding the first connection as the lobby creator
     *
     * @param player first player connection as creator
     */
    private synchronized void createLobby(ServerClientHandlerSocket player) {
        try {
            lobby = new Lobby();
            player.setCreator(true);
            lobby.setReloaded(false);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot create a lobby!", e);
            player.closeSocket();
            isActive = false;
        }
    }
    /*----------------------------------------------------------------------------------------------------------------*/



    /*-------------------------------------------------NEW GAME-------------------------------------------------------*/

    /**
     * Method that asks if the player wants to play a new game, when the previous one is ended
     *
     * @param player players's connection
     * @param demand the demand message sent by the player that contains his answer
     * @return {@code true} if the player wants to play again, {@code false} otherwise
     */
    synchronized boolean newGame(ServerClientHandlerSocket player, Demand demand) {
        if (demand == null) return false;

        String response = demand.getPayload().toString().equals("close")
                ? "close"
                : ((ReducedMessage) demand.getPayload()).getMessage();

        if (closeIfItIsTooLateToPlayAgain(player))
            return false; //not toRepeat because it has to stop

        if (response.equals("n")) {
            resetLobby();
            pendingPlayers.remove(player.getName());
            closeConnection(player);
            return false; //not toRepeat because it has to stop
        } else if (response.equals("y")) {
            resetLobby();
            pendingPlayers.remove(player.getName());
            player.setCreator(false);
            return false; //not toRepeat because a new game has to start
        } else if (!response.equals("close"))
            player.send(new Answer<>(AnswerType.ERROR));

        return true; //toRepeat
    }

    /**
     * Method that cleans the lobby and resets it
     */
    private synchronized void resetLobby() {
        if (!alreadyNewGame) {
            int lobbyHashCode = getLobbyPlayerListHashCode(lobby);
            int indexToRemove = loadedLobbyPathMap.get(lobbyHashCode);

            loadedLobbyMap.remove(lobbyHashCode);
            loadedLobbyPathMap.remove(lobbyHashCode);

            lobby.clean();
            lobby = null;

            try {
                Files.deleteIfExists(Paths.get(Lobby.BACKUP_PATH));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Got an IOException, couldn't delete lobby", e);
            }
            removeAndShiftLobbies(indexToRemove);

            pendingPlayers.putAll(connectedPlayers);
            connectedPlayers.clear();
            alreadyNewGame = true;
        }
    }

    private synchronized void closeConnection(ServerClientHandlerSocket player) {
        player.setLoggingOut(true); //so it won't disconnect the others
        player.closeSocket();
    }

    private synchronized void waitRestart(ServerClientHandlerSocket newCreator) {
        synchronized (newCreator.lockRestart) {
            try {
                while (!newCreator.isOkToRestart()) newCreator.lockRestart.wait();
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Got an unexpected InterruptedException", e);
                Thread.currentThread().interrupt();
                isActive = false;
            }
        }
    }

    private synchronized boolean closeIfItIsTooLateToPlayAgain(ServerClientHandlerSocket player) {
        if (lobby != null && pendingPlayers.containsValue(player) && !alreadyNewGame) {
            pendingPlayers.remove(player.getName());
            if (connectedPlayers.containsKey(player.getName())) {
                closeConnection(player);
                return true; //it is too late to "play again"
            }
        }

        return false; //it is not too late
    }

    synchronized void removeFromPending(ServerClientHandlerSocket player) {
        pendingPlayers.remove(player.getName());
    }
    /*----------------------------------------------------------------------------------------------------------------*/
}
