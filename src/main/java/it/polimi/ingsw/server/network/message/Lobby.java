package it.polimi.ingsw.server.network.message;

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
    private String ID;
    private final Game game;
    private final Controller controller;
    private final List<View> playerViewList;
    private String messagePath;
    private String backupPath;
    private final Map<ServerClientHandler, View> playingConnection;
    private static final Random randomLobby = new SecureRandom();

    public Lobby(int numberOfPlayers) throws ParserConfigurationException, SAXException {
        ID = String.valueOf(Math.abs(randomLobby.nextInt() + numberOfPlayers*randomLobby.nextInt()));
        //TODO
        //game = new Game(numberOfPlayers);
        game = new Game();
        controller = new Controller(game);
        playerViewList = new ArrayList<>();
        playingConnection = new HashMap<>();
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

    public void setID(String id) {
        this.ID = id;
    }

    public void deletePlayer(ServerClientHandler player) {
        playerViewList.remove(playingConnection.get(player));
        playingConnection.remove(player);
    }

    public void addPlayer(String player, ServerClientHandler serverClientHandler) {
        RemoteView v = new RemoteView(player, serverClientHandler);

        playerViewList.add(v);
        playingConnection.put(serverClientHandler, v);

        v.addObserver(controller);
        //TODO
        //game addObserver view
    }
}
