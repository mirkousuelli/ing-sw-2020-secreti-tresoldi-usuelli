package it.polimi.ingsw.server.network.message;

import it.polimi.ingsw.client.view.cli.Color;
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
    private final String ID;
    private final Game game;
    private final Controller controller;
    private final List<View> playerViewList;
    private String messagePath;
    private String backupPath;
    private final Map<ServerClientHandler, View> playingConnection;
    private final Map<View, ReducedPlayer> playerColor;
    private static final Random randomLobby = new SecureRandom();
    private int numberOfPlayers;

    public final Object lockLobby;

    public Lobby() throws ParserConfigurationException, SAXException {
        ID = String.valueOf(Math.abs(randomLobby.nextInt()));

        game = new Game();
        controller = new Controller(game);
        playerViewList = new ArrayList<>();
        playingConnection = new HashMap<>();
        playerColor = new HashMap<>();

        lockLobby = new Object();
        numberOfPlayers = 2; //default
    }

    public String getMessagePath() {
        return this.messagePath;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public String getID() {
        return ID;
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
        //TODO
    }

    public synchronized boolean addPlayer(String player, ServerClientHandler serverClientHandler) {
        if (playingConnection.size() == numberOfPlayers) return false;

        RemoteView v = new RemoteView(player, serverClientHandler);

        boolean toRepeat;
        Color color;
        do {
            toRepeat = false;
            color = Color.parseInt(randomLobby.nextInt(Color.values().length));
            for (View view: playerViewList) {
                if (playerColor.get(view).getColor().equals(color.toString())) {
                    toRepeat = true;
                    break;
                }
            }
        } while (toRepeat);

        playerViewList.add(v);
        playingConnection.put(serverClientHandler, v);
        playerColor.put(v, new ReducedPlayer(v.getPlayer(), color.toString()));

        v.addObserver(controller);
        //TODO
        //game addObserver view

        return true;
    }

    public synchronized boolean canStart() {
        return playingConnection.size() == numberOfPlayers;
    }
}
