package it.polimi.ingsw.server.network.message;

import it.polimi.ingsw.communication.Color;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.controller.Controller;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.network.ServerClientHandler;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.security.SecureRandom;
import java.util.*;

public class Lobby {
    private final String id;
    private final Game game;
    private final Controller controller;
    private final List<View> playerViewList;
    private String messagePath;
    private String backupPath;
    private final Map<ServerClientHandler, View> playingConnection;
    private final Map<View, ReducedPlayer> playerColor;
    private static final Random randomLobby = new SecureRandom();
    private int numberOfPlayers;
    private boolean reloaded;

    public final Object lockLobby;

    public Lobby() throws ParserConfigurationException, SAXException {
        this(new Game());
        reloaded = false;
    }

    public Lobby(Game game) {
        id = String.valueOf(randomLobby.nextInt());

        this.game = game;
        controller = new Controller(game);
        playerViewList = new ArrayList<>();
        playingConnection = new HashMap<>();
        playerColor = new HashMap<>();

        lockLobby = new Object();
        numberOfPlayers = 2; //default

        reloaded = true;
    }

    public String getMessagePath() {
        return this.messagePath;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public String getId() {
        return id;
    }

    public synchronized int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public synchronized List<ReducedPlayer> getReducedPlayerList() {
        List<ReducedPlayer> reducedPlayerList = new ArrayList<>();
        ReducedPlayer reducedPlayer;

        for (View v : playerViewList) {
            reducedPlayer = new ReducedPlayer(v.getPlayer());
            reducedPlayer.setColor(playerColor.get(v).getColor());
            reducedPlayerList.add(reducedPlayer);
        }

        return reducedPlayerList;
    }

    public synchronized void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public synchronized void deletePlayer(ServerClientHandler player) {
        playerViewList.remove(playingConnection.get(player));
        playingConnection.remove(player);
    }

    public synchronized void setCurrentPlayer(String player) {
        game.setCurrentPlayer(game.getPlayer(player));
    }

    public boolean isReloaded() {
        return reloaded;
    }

    public Game getGame() {
        return game;
    }

    public synchronized boolean addPlayer(String player, ServerClientHandler serverClientHandler) {
        if (playingConnection.size() == numberOfPlayers) return false;

        RemoteView v = new RemoteView(player, serverClientHandler);

        playerViewList.add(v);
        playingConnection.put(serverClientHandler, v);
        playerColor.put(v, new ReducedPlayer(v.getPlayer(), Color.values()[playerViewList.size()].toString()));

        v.addObserver(controller);
        game.addObserver(v);

        game.addPlayer(player);

        return true;
    }

    public synchronized boolean canStart() {
        return playerViewList.size() == numberOfPlayers;
    }

    public synchronized boolean isCurrentPlayerInGame(ServerClientHandler c) {
        return game.getCurrentPlayer().nickName.equals(playingConnection.get(c).getPlayer());
    }
}
