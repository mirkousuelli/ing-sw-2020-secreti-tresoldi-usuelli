package it.polimi.ingsw.model.state;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.state.GameState;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.cards.Deck;



public class Game{
    private Player currentPlayer;
    public final Deck deck;
    public final Board board;
    public final GameState gameState;

    public game (int numberOfPlayers, String[] Nicknames){
        /*
        *
         */
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setState(GameState state){
        this.state = state;
    }

    public boolean gameEngine(){
        /*
         *
         */
        return true;
    };
}