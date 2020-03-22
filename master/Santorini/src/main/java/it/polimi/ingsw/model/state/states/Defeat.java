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

public class Defeat implements GameState {
    /* @abstractClass
     * it represents the state where a player has lost
     */

    public Defeat(Game game) {
        /* @constructor
         * it tells a player that he lost and then eliminates him
         */
    }

    private void removePlayer(Game game) {
        /* @function
         * it eliminates the player that doesn't have any movable worker or cannot move with any of them
         */
    }

    private void removeWorker(Game game) {
        /* @function
         * it eliminates a worker that is chosen as target from God powers that can do this
         */
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }

}