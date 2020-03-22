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

import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Worker;
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class Move implements GameState {
    /* @abstractClass
     * it represents the state where a player has to move at least one of his worker
     */

    public Move(Game game) {
        /* @constructor
         * it allows a player to move to a certain cell with a specific worker
         */
    }

    private boolean movedWorker(Game game) {
        /* @predicate
         * it tells whether or not the player moved at least a worker in this turn
         */
        return true;
    }

    private boolean isCorrectMove(Cell cell) {
        /* @predicate
         * it tells if the move to the chosen cell is possible
         */
        return true;
    }

    private void removeWorker(Worker worker) {
        /* @function
         * it removes a worker from the game because of some God Power that can do it
         */
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}