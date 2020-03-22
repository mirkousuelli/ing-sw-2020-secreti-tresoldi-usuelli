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

    public ChangeTurn(Game game) {
        /* @constructor
         * it changes the turn and the new player has to move one of his worker
         */
    }

    private void changeCurrentPlayer(Game game) {
        /* @function
         * it switches the player that must play
         */
    }

    private boolean controlWinCondition(Game game) {
        /* @predicate
         * it checks if any win condition is verified (some God powers add a secondary wind condition)
         */
        return true;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}