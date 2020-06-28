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
import java.util.stream.Stream;

/**
 * Class which manages the server side connection protocol through sockets
 */
public class ServerConnectionSocket {

    private final int port;

    private final Map<String, ServerClientHandlerSocket> waitingConnection = new HashMap<>();

    private Lobby lobby;
    private boolean isActive;
    private boolean alreadyNewGame;

    private final Map<Integer, Lobby> loadedLobbyMap;
    private final Map<Integer, Integer> loadedLobbyPathMap;
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
        loadedLobbyMap = new HashMap<>();
        loadedLobbyPathMap = new HashMap<>();
        numOfLobbies = 0;
        loadLobbies();
        moveAndLoadLobbies();

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

    private void loadLobbies() {
        int numOfLobby = 0;

        if (!Files.exists(Paths.get(LOBBY_DIR))) return;

        try (Stream<Path> files = Files.list(Paths.get(LOBBY_DIR))) {
            numOfLobby = (int) files.count();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, couldn't count lobbies", e);
        }

        numOfLobbies = numOfLobby;
        Lobby lobbyLoaded;
        int lobbyHashCode;
        for (int i = 1; i <= numOfLobby; i++) {
            lobbyLoaded = loadLobby(i);
            if (lobbyLoaded != null) {
                lobbyHashCode = getLobbyPlayerListHashCode(lobbyLoaded);
                loadedLobbyMap.put(lobbyHashCode, lobbyLoaded);
                loadedLobbyPathMap.put(lobbyHashCode, i);
            }
        }
    }

    private int getLobbyPlayerListHashCode(Lobby lobby) {
        return lobby.getGame().getPlayerList().stream()
                .map(Player::getNickName)
                .collect(Collectors.toList())
                .hashCode();
    }

    /**
     * Method that loads all the previous lobbies in order to recover a past match that had been saved
     */
    private Lobby loadLobby(int i) {
        Game loadedGame = null;
        Lobby loadedLobby = null;

        try {
            if (Files.exists(Paths.get(LOBBY_NAME + i + EXTENSION))) {
                loadedGame = GameMemory.load(LOBBY_NAME + i + EXTENSION);
                if (loadedGame.getState().getName().equals(State.VICTORY.toString()))
                    loadedGame = null;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot load backup", e);
        }

        if (loadedGame != null) {
            loadedLobby = new Lobby(loadedGame);
            loadedLobby.setNumberOfPlayers(loadedGame.getNumPlayers());
        }

        return loadedLobby;
    }

    /**
     * Method that creates a new lobby by adding the first connection as the lobby creator
     *
     * @param player first player connection as creator
     */
    private void createLobby(ServerClientHandlerSocket player) {
        try {
            lobby = new Lobby();
            player.setCreator(true);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot create a lobby!", e);
            player.closeSocket();
            isActive = false;
        }

        numOfLobbies++;
    }

    /**
     * Method which manages a sudden disconnection case in order to correctly save the game in that scenario
     */
    void suddenDisconnection(ServerClientHandlerSocket disconnectedPlayer) {
        deletePlayer(disconnectedPlayer); //delete the disconnected player

        if (waitingConnection.isEmpty()) return; //safety check

        if (lobby.getNumberOfPlayers() > 0 && waitingConnection.size() + 1 > lobby.getGame().getNumPlayers()) { //a player (disconnectedPlayer) has been defeated but the are other players remaining (more than one)
            System.out.println("AAAAA");
            lobby.deletePlayer(disconnectedPlayer.getName());
            lobby.setNumberOfPlayers(lobby.getNumberOfPlayers() - 1);
        } else if (lobby.getNumberOfPlayers() == -1) { //creator disconnects before selecting the number of players
            System.out.println("BBBBBB");
            if (disconnectedPlayer.isCreator()) {
                ServerClientHandlerSocket newCreator = new ArrayList<>(waitingConnection.values()).get(0);

                newCreator.setCreator(true);
                newCreator.setIsToRestart(true);
                newCreator.send(new Answer<>(AnswerType.SUCCESS, UpdatedPartType.PLAYER));
                waitRestart(newCreator);
                newCreator.setOkToRestart(false);
                newCreator.callWatchDog(true); //restart the game for the new creator (other possible players in wait can remain in wait)
            }
        } else {
            System.out.println("CCCCCCC");
            //load lobbies if there at least one to load
            moveAndLoadLobbies();

            for (ServerClientHandlerSocket serverClientHandler : waitingConnection.values()) { //there was an unexpected disconnection, stop the match for all the players in game
                serverClientHandler.setIsToRestart(true);
                serverClientHandler.send(new Answer<>(AnswerType.CLOSE));
                waitRestart(serverClientHandler);
                serverClientHandler.setOkToRestart(false);
                serverClientHandler.callWatchDog(true); //restart the game for the remaining ones
            }

            //reset
            waitingConnection.clear();
        }
    }

    private void moveAndLoadLobbies() {
        if (lobby != null) {
            loadedLobbyMap.put(getLobbyPlayerListHashCode(lobby), lobby);
            loadedLobbyPathMap.put(getLobbyPlayerListHashCode(lobby), numOfLobbies);
        }
        lobby = null;

        if (!Files.exists(Paths.get(Lobby.BACKUP_PATH))) return;

        try {
            if (!Files.exists(Paths.get(LOBBY_DIR)))
                Files.createDirectory(Paths.get(LOBBY_DIR));
            Files.createFile(Paths.get(LOBBY_NAME + numOfLobbies + EXTENSION));
            Files.move(Paths.get(Lobby.BACKUP_PATH), Paths.get(LOBBY_NAME + numOfLobbies + EXTENSION), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, couldn't move file", e);
        }
    }

    /**
     * Method that controls if a player is present in a lobby
     *
     * @param name the nickname of the player to search
     * @return {@code true} if the player exists in a lobby, {@code false} otherwise
     */
    private Lobby isPlayerInALobby(String name) {
        return loadedLobbyMap.values().stream()
                .reduce(null, (lobby1, lobby2) -> lobby1 != null && lobby1.isPresentInGame(name)
                        ? lobby1
                        : lobby2);
    }

    /**
     * Method that operates the proper connection
     *
     * @param player player's connection
     * @param name   player's nickname
     * @return {@code true} if connected successfully, {@code false} if the connection went wrong
     */
    synchronized boolean connect(ServerClientHandlerSocket player, String name) {
        if (!loadedLobbyMap.isEmpty() || lobby != null) { //if some lobbies where loaded, maybe 'player' is in one of them
            if (lobby != null) { //if 'player' isn't the first one to connect (which means another player loaded lobby or created a new one)
                if (lobby.isReloaded()) { //if lobby is reloaded
                    if (connectReload(player, name))
                        return true; //toRepeat
                } else { //else it is a new lobby
                    Boolean toRepeat = connectBasic(player, name); //if toRepeat is null there is still something to do
                    if (toRepeat != null)
                        return toRepeat;
                }
            } else { //'player' is the first one to connect
                Lobby lobbyLoaded = isPlayerInALobby(name); //search a lobby with player
                if (lobbyLoaded != null) { //if 'player' is in a lobby
                    lobby = lobbyLoaded; //then that lobby will be the one that will be used for this match
                    if (connectReload(player, name))
                        return true; //toRepeat
                    else
                        removeLobbyFromLobbyDir(lobby);
                } else
                    createLobby(player); //else create a new lobby
            }
        } else //creator (no lobbies to load)
            createLobby(player);

        waitingConnection.put(name, player); //creator or joiner

        if (canStart()) //add everyone to the game if the number of players is reached
            startMatch();

        if (!isLobbyReloaded())
            player.send(new Answer<>(AnswerType.SUCCESS, new ReducedPlayer(name, player.isCreator())));

        return false; //not toRepeat
    }

    private void removeLobbyFromLobbyDir(Lobby lobby) {
        int lobbyHashCode = getLobbyPlayerListHashCode(lobby);
        Integer numberOfLobby = loadedLobbyPathMap.get(lobbyHashCode);
        if (numberOfLobby == null) return;

        int i = numberOfLobby + 1;
        try {
            while (Files.exists(Paths.get(LOBBY_NAME + i + EXTENSION))) {
                Files.move(Paths.get(LOBBY_NAME + i + EXTENSION), Paths.get(LOBBY_NAME + (i - 1) + EXTENSION), StandardCopyOption.REPLACE_EXISTING);
                i++;
            }
            Files.deleteIfExists(Paths.get(LOBBY_NAME + (i - 1) + EXTENSION));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, couldn't delete a lobby");
        }

        loadedLobbyMap.remove(lobbyHashCode);
        loadedLobbyPathMap.remove(lobbyHashCode);
        if (numOfLobbies > 0)
            numOfLobbies--;
    }

    private synchronized Boolean connectBasic(ServerClientHandlerSocket player, String name) {
        Boolean toRepeat = null;

        if (waitingConnection.get(name) != null) { //userName already exists --> changeName or exit client-side
            player.send(new Answer<>(AnswerType.ERROR));
            toRepeat = true; //toRepeat
        } else if (waitingConnection.keySet().size() == lobby.getNumberOfPlayers()) { //lobby full --> exit server-side
            player.closeSocket();
            player.setLoggingOut(false);
            toRepeat = false; //not toRepeat because it has to stop
        }

        return toRepeat;
    }

    private synchronized boolean connectReload(ServerClientHandlerSocket player, String name) {
        if (!lobby.isPresentInGame(name)) {
            if (!waitingConnection.keySet().isEmpty()) { //not present in reloaded lobby and someone is already waiting --> changeName or exit client-side
                player.send(new Answer<>(AnswerType.ERROR));
                return true; //toRepeat
            } else //not present in reloaded lobby but no waiting players, so create a new lobby
                createLobby(player);
        }

        return false;
    }

    /**
     * Method that starts the game
     */
    private void startMatch() {
        AtomicInteger i = new AtomicInteger();
        alreadyNewGame = false;

        waitingConnection.values().stream()
                .sorted(Comparator.comparing(ServerClientHandlerSocket::isCreator, Comparator.reverseOrder()))
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
     * Method that approves the intention to start the game checking all the conditions
     *
     * @return {@code true} if the game can start, {@code false} otherwise
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
     * @param player creator's connection
     * @param demand demand message received containing information about the number of players
     * @return {@code true} if something went wrong, {@code false} if the match started
     */
    synchronized boolean numOfPlayers(ServerClientHandlerSocket player, Demand demand) {
        if (demand == null) return false;

        String value = ((ReducedMessage) demand.getPayload()).getMessage();
        int numOfPls = Integer.parseInt(value);

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
     * Method that asks if the player wants to play a new game, when the previous one is ended
     *
     * @param player players's connection
     * @param demand the demand message sent by the player that contains his answer
     * @return {@code true} if the player wants to play again, {@code false} otherwise
     */
    synchronized boolean newGame(ServerClientHandlerSocket player, Demand demand) {
        if (demand == null) return false;

        String response = ((ReducedMessage) demand.getPayload()).getMessage();

        if (lobby != null && waitingConnection.size() > 0 && lobby.getNumberOfPlayers() > 0 && lobby.getNumberOfPlayers() < waitingConnection.size()) {
            closeConnection(player);
            return false; //not toRepeat
        }

        if (response.equals("n")) {
            resetLobby();
            closeConnection(player);
            return false;
        } else if (response.equals("y")) {
            resetLobby();
            player.setCreator(false);
            return false;
        }

        player.send(new Answer<>(AnswerType.ERROR));
        return true;
    }

    /**
     * Method that cleans the lobby and resets it
     */
    private void resetLobby() {
        if (!alreadyNewGame) {
            lobby.clean();
            lobby = null;
            try {
                Files.deleteIfExists(Paths.get(Lobby.BACKUP_PATH));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Got an IOException, couldn't delete lobby", e);
            }
            waitingConnection.clear();
            alreadyNewGame = true;
        }
    }

    private void closeConnection(ServerClientHandlerSocket player) {
        player.setLoggingOut(true);
        player.closeSocket();
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

    Lobby getLobby() {
        Lobby toReturn;

        synchronized (this) {
            toReturn = lobby;
        }

        return toReturn;
    }

    void deletePlayer(ServerClientHandlerSocket player) {
        synchronized (waitingConnection) {
            waitingConnection.remove(player.getName());
            lobby.deletePlayer(player);
        }
    }

    private void waitRestart(ServerClientHandlerSocket newCreator) {
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
}
