package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JGod;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JGame {
    private final List<JPlayer> players;
    private int current;

    public JGame(){
        this.players = new ArrayList<>();
    }

    public void addPlayer(String nickname, int index) {
        this.players.add(new JPlayer(nickname, index));
    }

    public JPlayer getPlayer(int index) {
        return players.get(index);
    }

    public void setCurrentPlayer(JPlayer chosen) {
        if (players.contains(chosen)) {
            if (current < this.getNumPlayer())
                this.players.get(current).disactive();
            this.current = this.players.indexOf(chosen);
            this.players.get(current).active();
        }
    }

    public JPlayer getCurrentPlayer() {
        return this.players.get(this.current);
    }

    public List<JPlayer> getPlayerList() {
        return this.players;
    }

    public int getNumPlayer() {
        return this.players.size();
    }
}
