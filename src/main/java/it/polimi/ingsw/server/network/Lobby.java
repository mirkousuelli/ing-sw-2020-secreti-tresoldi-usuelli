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
import java.util.*;
import java.util.stream.Collectors;

public class Lobby {

    private final Game game;
    private final Controller controller;

    private int numberOfPlayers;
    private boolean reloaded;

    private final List<View> playerViewList;
    private final Map<ServerClientHandler, View> playingConnection;
    private final Map<View, ReducedPlayer> playerColor;

    final Object lockLobby;

    public static final String backupPath = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_lobby.xml";

    Lobby() throws ParserConfigurationException, SAXException {
        this(new Game());
        reloaded = false;

        File f = new File(backupPath);
        boolean b;
        if (f.exists())
            b = f.delete();
    }

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

    private ServerClientHandler getServerClientHandler(View v) {
        return playingConnection.keySet().stream()
                .filter(c -> playingConnection.get(c).equals(v))
                .reduce(null, (a, b) -> a != null ? a : b);
    }

    void setNumberOfPlayers(int numberOfPlayers) {
        synchronized (lockLobby) {
            this.numberOfPlayers = numberOfPlayers;
        }
    }

    void deletePlayer(ServerClientHandler player) {
        synchronized (playingConnection) {
            View playerToRemove = playingConnection.get(player);

            playerToRemove.removeObserver(controller);
            game.removeObserver(playerToRemove);

            game.removePlayer(playerToRemove.getPlayer());
            synchronized (playerViewList) {
                playerViewList.remove(playerToRemove);
            }
            playingConnection.remove(player);
        }
    }

    void setCurrentPlayer(String player) {
        synchronized (game) {
            game.setCurrentPlayer(game.getPlayer(player));
        }
    }

    boolean isReloaded() {
        boolean ret;

        synchronized (lockLobby) {
            ret = reloaded;
        }

        return ret;
    }

    boolean isFull() {
        return numberOfPlayers == playerViewList.size();
    }

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

    boolean isCurrentPlayerInGame(ServerClientHandler c) {
        boolean ret;

        synchronized (game) {
            synchronized (playingConnection) {
                ret = game.getCurrentPlayer().nickName.equals(playingConnection.get(c).getPlayer());
            }
        }

        return ret;
    }

    boolean isPresentInGame(ServerClientHandler c) {
        return playingConnection.get(c) != null;
    }

    boolean isPresentInGame(String name) {
        return game.getPlayer(name) != null;
    }

    String getColor(ServerClientHandler c) {
        String color;

        synchronized (playerColor) {
            synchronized (playingConnection) {
                color = playerColor.get(playingConnection.get(c)).getColor();
            }
        }

        return color;
    }

    String getPlayer(ServerClientHandler c) {
        return playerColor.get(playingConnection.get(c)).getNickname();
    }

    List<ServerClientHandler> getServerClientHandlerList() {
        return new ArrayList<>(playingConnection.keySet());
    }

    ServerClientHandler setNewCreator(ServerClientHandler c) {
        return playingConnection.keySet().stream().filter(serverClientHandler -> !serverClientHandler.equals(c)).collect(Collectors.toList()).get(0);
    }

    void cleanGame() {
        game.clean();
    }
}
