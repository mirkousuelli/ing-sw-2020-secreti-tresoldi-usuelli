package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.game.states.Start;
import it.polimi.ingsw.server.network.message.Lobby;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

public class Game {
    /* @class
     * it contains all the information useful for the game that is being played
     */
    private final Lobby lobby;
    private Deck deck;
    private Board board;
    private GameState state;
    private int currentPlayer;

    public Game() throws ParserConfigurationException, SAXException {
        /* @constructor
         * it is used to create a new game, initialising its state to start
         */
        this.lobby = new Lobby();
        this.deck = new Deck();
        this.board = new Board();
        this.state = new Start(this);
    }

    public void assignCard(God god) {
        this.deck.fetchCard(god);
        this.lobby.getPlayer(this.currentPlayer).setCard(this.deck.popCard(god));
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
        this.currentPlayer = this.lobby.getIndex(currentPlayer);
    }

    public Player getCurrentPlayer() {
        /* @getter
         * it gets the current player
         */
        return this.lobby.getPlayer(this.currentPlayer);
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

    public Lobby getLobby() {
        return this.lobby;
    }

    public void gameEngine() {
        /*
         *
         */
    }
}