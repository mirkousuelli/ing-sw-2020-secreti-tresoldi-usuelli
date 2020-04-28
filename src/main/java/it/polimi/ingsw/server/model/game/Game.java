package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.map.Board;
//import it.polimi.ingsw.server.model.game.states.Start;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

public class Game {
    /* @class
     * it contains all the information useful for the game that is being played
     */
    List<Player> players;
    private Deck deck;
    private Board board;
    private /*GameState*/ State state;
    private int currentPlayer;
    private int numPlayers;

    public Game() throws ParserConfigurationException, SAXException {
        /* @constructor
         * it is used to create a new game, initialising its state to start
         */
        this.deck = new Deck();
        this.board = new Board();
        this.state = State.START;//new Start(this);
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public int getNumPlayers() {
        return this.numPlayers;
    }

    public void addPlayer(String nickname) {
        if (players.size() < numPlayers)
            this.players.add(new Player(nickname));
        numPlayers++;
    }

    public Player getPlayer(String nickname) {
        for (Player p : this.players) {
            if (p.getNickName().equals(nickname))
                return p;
        }
        return null;
    }

    public Player getPlayer(int index) {
        if (index >= 0 && index < numPlayers)
            return this.players.get(index);
        return null;
    }

    public int getIndex(Player player) {
        return this.players.indexOf(player);
    }

    public void assignCard(God god) {
        this.deck.fetchCard(god);
        this.players.get(this.currentPlayer).setCard(this.deck.popCard(god));
    }

    public Board getBoard() {
        /* @getter
         * it gets the board for the game
         */
        return board;
    }

    public void setBoard(Board newBoard) {
        this.board = newBoard;
    }

    public Deck getDeck() {
        /* @getter
         * it gets the deck of cards in use
         */
        return deck;
    }

    public void setDeck(Deck newDeck) {
        this.deck = newDeck;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        /* @setter
         * it sets the current player to the designated one
         */
        this.currentPlayer = this.players.indexOf(currentPlayer);
    }

    public Player getCurrentPlayer() {
        /* @getter
         * it gets the current player
         */
        return this.players.get(this.currentPlayer);
    }

    public State getState() {
        /* @getter
         * it gets the current state of the game
         */
        return state;
    }

    public void setState(/*GameState*/State state) {
        /* @setter
         * it sets the current state to the designated one
         */
        this.state = state;
    }

    public void gameEngine() {
        /*
         *
         */
    }
}