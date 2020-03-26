/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.state.states;

import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class ChangeTurn implements GameState {
    /* @abstractClass
     * it represents the state where the current player changes and the win conditions are checked
     */

    public Game game;

    public ChangeTurn(Game game) {
        /* @constructor
         * it changes the turn and the new player has to move one of his worker
         */
        this.game = game;

        // Check if any win condition is verified (or if only one player remains; if so the game goes to Victory state
        if(controlWinCondition(game) || onePlayerRemaining(game))
            game.setState(new Victory(game));
         else {
             // Otherwise the current player is changed
            changeCurrentPlayer(game);
        }

        // If the new player can move one of his workers, the game goes to ChooseWorker state
        game.setState(new ChooseWorker(game));
        // otherwise the game goes to Defeat state
        game.setState(new Defeat(game));

    }

    private void changeCurrentPlayer(Game game) {
        /* @function
         * it switches the player that must play
         */
    }

    private boolean onePlayerRemaining(Game game){
        return game.numPlayerRemaining == 1;
    }

    private boolean controlWinCondition(Game game) {
        /* @predicate
         * it checks if any win condition is verified (some God powers add a secondary win condition)
         */
        return false;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}