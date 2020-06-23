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

    public JGame(JDeck deck) {
        players = new ArrayList<>();
        this.deck = deck;
        map = new JMap();
    }

    public JGame() {
        this(new JDeck());
    }

    public void addPlayer(String nickname, int index) {
        players.add(new JPlayer(nickname, index));
    }

    public JPlayer getPlayer(int index) {
        return players.get(index);
    }

    public JPlayer getPlayer(String name) {
        return players.stream()
                .filter(jPlayer -> jPlayer.getNickname().equals(name))
                .reduce(null, (a, b) -> a != null
                        ? a
                        : b
                );
    }

    public void setCurrentPlayer(JPlayer chosen) {
        if (players.contains(chosen)) {
            if (current < getNumPlayer())
                players.get(current).disactive();
            current = players.indexOf(chosen);
            players.get(current).active();
            map.setCurrentPlayer(players.get(current));
        }
    }

    public void setCurrentPlayer(String name) {
        JPlayer newCurrentPlayer = getPlayer(name);
        setCurrentPlayer(newCurrentPlayer);
    }

    public JPlayer getCurrentPlayer() {
        return players.get(current);
    }

    public List<JPlayer> getPlayerList() {
        return new ArrayList<>(players);
    }

    public void removePlayer(String playerName) {
        JPlayer playerToRemove = getPlayer(playerName);

        playerToRemove.removeWorkers();
        players.remove(playerToRemove);

        if (current > players.size() - 1)
            current--;
    }

    public int getNumPlayer() {
        return players.size();
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

    public void clean() {
        players.clear();
        current = 0;
        deck.clean();
        map.clean();
    }
}
