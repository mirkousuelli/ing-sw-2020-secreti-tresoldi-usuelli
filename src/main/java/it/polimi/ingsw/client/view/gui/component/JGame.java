package it.polimi.ingsw.client.view.gui.component;

import java.util.ArrayList;
import java.util.List;

public class JGame {
    private final List<JPlayer> players;

    public JGame(){
        this.players = new ArrayList<>();
    }

    public void addPlayer(String nickname) {
        this.players.add(new JPlayer(nickname));
    }

    public JPlayer getPlayer(int index) {
        return players.get(index);
    }
}
