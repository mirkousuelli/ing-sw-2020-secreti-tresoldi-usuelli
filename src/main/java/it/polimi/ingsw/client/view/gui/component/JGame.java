package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.client.view.gui.component.map.JMap;

import java.util.ArrayList;
import java.util.List;

public class JGame {
    private final List<JPlayer> players;
    private int current;
    private JDeck deck;
    private final JMap map;

    public JGame(JDeck deck){
        this.players = new ArrayList<>();
        this.deck = deck;
        map = new JMap();
    }

    public JGame(){
        this(new JDeck());
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

    public void setCurrentPlayer(String name) {
        JPlayer newCurrentPlayer = players.stream()
                .filter(p -> p.getNickname().equals(name))
                .reduce(null, (a, b) -> a!= null
                        ? a
                        : b
                );

        current = players.indexOf(newCurrentPlayer);
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

    public JDeck getJDeck() {
        return deck;
    }

    public void setJDeck(JDeck deck) {
        this.deck = deck;
    }

    public JMap getJMap() {
        return map;
    }
}
