package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.map.Board;

public class Game {
    public final Deck deck;
    public final Board board;
    private Player currentPlayer;
    private GameState state;

    public Game (int numberOfPlayers, String[] Nicknames) {
        /*
        *
         */
        deck = null;
        board = null;
        state = null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public boolean gameEngine() {
        /*
         *
         */
        return true;
    }
}