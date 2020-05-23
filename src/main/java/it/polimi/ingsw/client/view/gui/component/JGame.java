package it.polimi.ingsw.client.view.gui.component;

import java.util.ArrayList;
import java.util.List;

public class JGame {
    private final List<JPlayer> players;
    private int current;

    public JGame(){
        this.players = new ArrayList<>();
    }

    public void addPlayer(String nickname) {
        this.players.add(new JPlayer(nickname));
    }

    public JPlayer getPlayer(int index) {
        return players.get(index);
    }

    public void setCurrentPlayer(int i) {
        this.current = i;
    }

    public void setCurrentPlayer(JPlayer player) {
        this.current = this.players.indexOf(player);
    }

    public JPlayer getCurrentPlayer() {
        return this.players.get(this.current);
    }

    public List<JPlayer> getList() {
        return this.players;
    }
}
