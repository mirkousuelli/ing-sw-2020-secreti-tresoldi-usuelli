package it.polimi.ingsw.server.network.message;

import it.polimi.ingsw.server.model.Player;

import java.util.List;

public class Lobby {
    List<Player> players;
    private String ID;
    private int NUM_PLAYERS;
    private String messagePath;
    private String backupPath;

    public Lobby(){}

    public String getMessagePath() {
        return this.messagePath;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public String getID() {
        return ID;
    }

    public int getNumPlayers() {
        return NUM_PLAYERS;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public void setNumPlayer(int numPlayer) {
        this.NUM_PLAYERS = numPlayer;
    }

    public void addPlayer(String nickname) {
        if (players.size() < NUM_PLAYERS)
            this.players.add(new Player(nickname));
    }

    public Player getPlayer(String nickname) {
        for (Player p : this.players) {
            if (p.getNickName().equals(nickname))
                return p;
        }
        return null;
    }

    public Player getPlayer(int index) {
        if (index >= 0 && index < NUM_PLAYERS)
            return this.players.get(index);
        return null;
    }

    public int getIndex(Player player) {
        return this.players.indexOf(player);
    }
}
