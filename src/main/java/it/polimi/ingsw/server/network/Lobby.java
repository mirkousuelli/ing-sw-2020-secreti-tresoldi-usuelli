package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which includes the proper game object reported to specific players.
 */
public class Lobby {

    private final Game game;
    private final Controller controller;

    private int numberOfPlayers;
    private boolean reloaded;

    private final List<View> playerViewList;
    private final Map<ServerClientHandler, View> playingConnection;
    private final Map<View, ReducedPlayer> playerColor;

    final Object lockLobby;

    public static final String BACKUP_PATH = "backup_lobby.xml";
    private static final Logger LOGGER = Logger.getLogger(Lobby.class.getName());

    /**
     * Constructor which creates the game and set up persistence feature
     */
    Lobby() throws ParserConfigurationException, SAXException {
        this(new Game());
        reloaded = false;

        removeBackUp();
    }

    /**
     * Constructor which is used to reload a previous game given as parameter
     *
     * @param game loaded from xml file
     */
    public Lobby(Game game) {
        this.game = game;
        controller = new Controller(game);

        playerViewList = new ArrayList<>();
        playingConnection = new HashMap<>();
        playerColor = new HashMap<>();

        lockLobby = new Object();
        numberOfPlayers = -1;

        reloaded = true;
    }

    int getNumberOfPlayers() {
        int ret;

        synchronized (lockLobby) {
            ret = numberOfPlayers;
        }

        return ret;
    }

    public List<ReducedPlayer> getReducedPlayerList() {
        List<ReducedPlayer> reducedPlayerList = new ArrayList<>();
        ReducedPlayer reducedPlayer;

        synchronized (playerViewList) {
            synchronized (playerColor) {
                for (View v : playerViewList) {
                    reducedPlayer = new ReducedPlayer(v.getPlayer());
                    reducedPlayer.setColor(playerColor.get(v).getColor());
                    reducedPlayer.setCreator(getServerClientHandler(v).isCreator());
                    reducedPlayerList.add(reducedPlayer);
                }
            }
        }

        return reducedPlayerList;
    }

    private ServerClientHandler getServerClientHandler(View view) {
        return playingConnection.keySet().stream()
                .filter(c -> playingConnection.get(c).equals(view))
                .reduce(null, (a, b) -> a != null ? a : b);
    }

    void setNumberOfPlayers(int numberOfPlayers) {
        synchronized (lockLobby) {
            this.numberOfPlayers = numberOfPlayers;
        }
    }

    /**
     * Method that removes players connection from the server
     *
     * @param player the player connection that is removed
     */
    void deletePlayer(ServerClientHandler player) {
        if (!playingConnection.containsKey(player)) return;

        View playerToRemove = playingConnection.get(player);

        playerToRemove.removeObserver(controller);
        game.removeObserver(playerToRemove);

        game.removePlayer(playerToRemove.getPlayer());
        playerViewList.remove(playerToRemove);

        playingConnection.remove(player);
        playerColor.remove(playerToRemove);
    }

    void setCurrentPlayer(String player) {
        synchronized (game) {
            game.setCurrentPlayer(game.getPlayer(player));
        }
    }

    /**
     * Method that says if the game (with its relative lobby) has been reloaded with success
     *
     * @return {@code true} if the lobby's game has been reloaded, {@code false} if the lobby's game has not been reloaded yet
     */
    boolean isReloaded() {
        boolean ret;

        synchronized (lockLobby) {
            ret = reloaded;
        }

        return ret;
    }

    /**
     * Method that says if the maximum number of players has been reached
     *
     * @return {@code true} no player to be added anymore, {@code false} there is still space for other player/s
     */
    boolean isFull() {
        int playerViewListSize;
        int numberOfPlayersNum;

        synchronized (playerViewList) {
            playerViewListSize = playerViewList.size();
        }

        synchronized (lockLobby) {
            numberOfPlayersNum = numberOfPlayers;
        }

        return numberOfPlayersNum == playerViewListSize;
    }

    public synchronized Game getGame() {
        return game;
    }

    public synchronized Controller getController() {
        return controller;
    }

    /**
     * Method that adds the player's nickname with its own connection
     *
     * @param player player's connection
     */
    synchronized void addPlayer(ServerClientHandler player) {
        if (playingConnection.size() == numberOfPlayers) return;

        RemoteView view = new RemoteView(player.getName(), player);

        playerViewList.add(view);
        playingConnection.put(player, view);
        playerColor.put(view, new ReducedPlayer(view.getPlayer(), Color.values()[playerViewList.size()].toString()));

        view.addObserver(controller);
        game.addObserver(view);

        if (!reloaded)
            game.addPlayer(player.getName());
    }

    /**
     * Method that check if the current player is connected inside the game
     *
     * @param player current player's connection
     * @return {@code true} if it is connected, {@code false} if it is not
     */
    boolean isCurrentPlayerInGame(ServerClientHandler player) {
        boolean ret;

        synchronized (game) {
            synchronized (playingConnection) {
                ret = game.getCurrentPlayer().nickName.equals(playingConnection.get(player).getPlayer());
            }
        }

        return ret;
    }

    /**
     * Method that check if a player is present in the game, given its nickname
     *
     * @param name players' nickname
     * @return {@code true} it is present, {@code false} it is not
     */
    boolean isPresentInGame(String name) {
        return game.getPlayer(name) != null;
    }

    /**
     * Method that sets lobby's parameter to an idle state
     */
    void clean() {
        playerViewList.forEach(v -> v.removeObserver(controller));
        playerViewList.forEach(game::removeObserver);
        playerColor.clear();
        playerViewList.clear();
        playingConnection.clear();
        reloaded = false;
        numberOfPlayers = -1;
        game.clean();
    }

    /**
     * Method that deletes the backup (if it exists)
     */
    private void removeBackUp() {
        try {
            Files.deleteIfExists(Paths.get(BACKUP_PATH));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
        }
    }
}
