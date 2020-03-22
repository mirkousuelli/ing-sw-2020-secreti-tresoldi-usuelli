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

public class Victory implements GameState {
    /* @abstractClass
     * it represents the state where a player wins
     */

    public Victory(Game game){
        /* @constructor
         * it shows the player that won and ends the game
         */
    }

    public void endGame(Game game) {
        /* @function
         * it is used after a player won, in order to end the current game
         */
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}