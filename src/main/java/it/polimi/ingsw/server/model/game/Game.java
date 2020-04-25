package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.God;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.game.states.Start;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class Game {
    /* @class
     * it contains all the information useful for the game that is being played
     */
    private List<Player> players;
    private Deck deck;
    private Board board;
    private GameState state;
    private int currentPlayer;

    public Game() throws ParserConfigurationException, SAXException {
        /* @constructor
         * it is used to create a new game, initialising its state to start
         */
        players = new ArrayList<>();
        deck = new Deck();
        board = new Board();
        state = new Start(this);
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
        this.currentPlayer = players.indexOf(currentPlayer);
    }

    public Player getCurrentPlayer() {
        /* @getter
         * it gets the current player
         */
        return this.players.get(this.currentPlayer);
    }

    public GameState getState() {
        /* @getter
         * it gets the current state of the game
         */
        return state;
    }

    public void setState(GameState state) {
        /* @setter
         * it sets the current state to the designated one
         */
        this.state = state;
    }

    public void addPlayer(String nickname) {
        this.players.add(new Player(nickname));
    }

    public Player getPlayer(String nickname) {
        for (Player p : this.players) {
            if (p.getNickName().equals(nickname))
                return p;
        }
        return null;
    }

    public void gameEngine() {
        /*
         *
         */
    }
}