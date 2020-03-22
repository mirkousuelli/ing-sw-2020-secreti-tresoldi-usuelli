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

public class Build implements GameState {
    /* @abstractClass
     * it represents the state where a player must (or can, in case of some God powers) build a block with his worker
     */

    public Build(Game game) {
        /* @constructor
         * it allows a player to build on a certain cell with a specific worker
         */
    }

    private void noAllowedBuilds() {
        /* @function
         * if the player cannot build with any of his workers, he automatically loses
         */
    }

    private void cannotBuildWithWorker() {
        /* @function
         * if the player cannot build with the selected worker he is told so and can choose to move the other worker in order to build with him
         */
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}