package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.deck.JDeck;
import it.polimi.ingsw.client.view.gui.component.map.JMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the game in the GUI.
 * <p>
 * It contains the list if players in the game, the index of the current one, the deck that is being used and the map.
 */
public class JGame {

    private final List<JPlayer> players;
    private int current;
    private JDeck deck;
    private JMap map;

    /**
     * Constructor of the JGame, creating the list of players, the deck and the map (all empty)
     *
     * @param deck the deck that is used in the game
     */
    public JGame(JDeck deck) {
        players = new ArrayList<>();
        this.deck = deck;
        map = new JMap();
    }

    public JGame() {
        this(new JDeck());
    }

    /**
     * Method that adds the chosen player to the game, given its nickname and index
     *
     * @param nickname the nickname of the player that is added
     * @param index    the index of the added player
     */
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

    /**
     * Method that removes the player (with his workers) from the list of players in the game
     *
     * @param playerName
     */
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

    /**
     * Method that cleans the game: it removes every player (resetting also each one of them) and the current
     * player's index, cleans the deck and the map.
     */
    public void clean() {
        players.forEach(JPlayer::clean);
        players.clear();
        current = 0;
        deck.clean();

        map.clean();
        map.removeAll();
        map.revalidate();
        map = new JMap();
    }
}
