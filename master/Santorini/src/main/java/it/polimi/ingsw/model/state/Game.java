/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.map.Board;

public class Game {
    /* @class
     * it contains all the information useful for the game that is being played
     */

    public final Deck deck;
    public final Board board;
    private Player currentPlayer;
    private GameState state;


    public Game(int numberOfPlayers, String[] Nicknames) {
        /* @constructor
         * it is used to create a new game, initialising its state to start
         */
        deck = null;
        board = null;
        state = null;
    }

    public Board getBoard() {
        /* @getter
         * it gets the board for the game
         */
        return board;
    }

    public Deck getDeck() {
        /* @getter
         * it gets the deck of cards in use
         */
        return deck;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        /* @setter
         * it sets the current player to the designated one
         */
        this.currentPlayer = currentPlayer;
    }

    public Player getCurrentPlayer() {
        /* @getter
         * it gets the current player
         */
        return currentPlayer;
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

    public void gameEngine() {
        /*
         *
         */
    }
}