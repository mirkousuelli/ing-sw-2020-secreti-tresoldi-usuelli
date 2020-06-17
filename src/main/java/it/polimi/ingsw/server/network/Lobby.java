package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which includes hte proper game object reported to specific players.
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

    public static final String backupPath = "backup_lobby.xml";

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

    /**
     * Method that gets the current number of player
     */
    int getNumberOfPlayers() {
        int ret;

        synchronized (lockLobby) {
            ret = numberOfPlayers;
        }

        return ret;
    }

    /**
     * Method that gets the current player list in a reduced format
     */
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

    /**
     * Method that establishes the proper connection server side with the set of players
     *
     * @param view used to match players' connection
     */
    private ServerClientHandler getServerClientHandler(View view) {
        return playingConnection.keySet().stream()
                .filter(c -> playingConnection.get(c).equals(view))
                .reduce(null, (a, b) -> a != null ? a : b);
    }

    /**
     * Method that sets the current number of players
     *
     * @param numberOfPlayers new number of players
     */
    void setNumberOfPlayers(int numberOfPlayers) {
        synchronized (lockLobby) {
            this.numberOfPlayers = numberOfPlayers;
        }
    }

    /**
     * Method that removes players connection from the server
     *
     * @param player the player connection that is going to be remove
     */
    void deletePlayer(ServerClientHandler player) {
        synchronized (playingConnection) {
            if(!playingConnection.containsKey(player)) return;

            View playerToRemove = playingConnection.get(player);

            playerToRemove.removeObserver(controller);
            game.removeObserver(playerToRemove);

            game.removePlayer(playerToRemove.getPlayer());
            synchronized (playerViewList) {
                playerViewList.remove(playerToRemove);
            }
            playingConnection.remove(player);
            playerColor.remove(playerToRemove);
        }
    }

    /**
     * Method that sets the current player
     *
     * @param player new current player
     */
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
        return numberOfPlayers == playerViewList.size();
    }

    /**
     * Method that sets if the lobby's game has been reloaded
     *
     * @param reloaded if reloaded
     */
    void setReloaded(boolean reloaded) {
        synchronized (lockLobby) {
            this.reloaded = reloaded;
        }
    }

    public Game getGame() {
        return game;
    }

    public synchronized Controller getController() {
        return controller;
    }

    /**
     * Method that adds the player's nickname with its own connection
     *
     * @param player player's nickname
     * @param serverClientHandler player's connection
     */
    synchronized void addPlayer(String player, ServerClientHandler serverClientHandler) {
        if (playingConnection.size() == numberOfPlayers) return;

        RemoteView v = new RemoteView(player, serverClientHandler);

        playerViewList.add(v);
        playingConnection.put(serverClientHandler, v);
        playerColor.put(v, new ReducedPlayer(v.getPlayer(), Color.values()[playerViewList.size()].toString()));

        v.addObserver(controller);
        game.addObserver(v);

        if (!reloaded)
            game.addPlayer(player);
    }

    /**
     * Method that check if the current player is connected inside the game
     *
     * @param c current player's connection
     * @return {@code true} if it is connected, {@code false} if it is not
     */
    boolean isCurrentPlayerInGame(ServerClientHandler c) {
        boolean ret;

        synchronized (game) {
            synchronized (playingConnection) {
                ret = game.getCurrentPlayer().nickName.equals(playingConnection.get(c).getPlayer());
            }
        }

        return ret;
    }

    /**
     * Method that check if a player is present by its connection
     *
     * @param c players' connection
     * @return {@code true} it is present, {@code false} it is not
     */
    boolean isPresentInGame(ServerClientHandler c) {
        return playingConnection.get(c) != null;
    }

    /**
     * Method that check if a player is present by its nickname
     *
     * @param name players' nickname
     * @return {@code true} it is present, {@code false} it is not
     */
    boolean isPresentInGame(String name) {
        return game.getPlayer(name) != null;
    }

    /**
     * Method that gets current players' connections list
     *
     * @return {@code ServerClientHandler} list of connections of current players of the lobby
     */
    List<ServerClientHandler> getServerClientHandlerList() {
        return new ArrayList<>(playingConnection.keySet());
    }

    /**
     * Method that lobby's parameter to an idle state
     */
    void clean() {
        playerViewList.forEach(v -> v.removeObserver(controller));
        playerViewList.forEach(game::removeObserver);
        playerColor.clear();
        playerViewList.clear();
        playingConnection.clear();
        reloaded = false;
        numberOfPlayers = -1;
        removeBackUp();
        game.clean();
    }

    private void removeBackUp() {
        try {
            Files.deleteIfExists(Paths.get(backupPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
