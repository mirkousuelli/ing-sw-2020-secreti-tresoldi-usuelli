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

import it.polimi.ingsw.model.exceptions.map.MapDimensionException;
import it.polimi.ingsw.model.exceptions.map.NotValidCellException;
import it.polimi.ingsw.model.map.Worker;
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class Build implements GameState {
    /* @abstractClass
     * it represents the state where a player must (or can, in case of some God powers) build a block with his worker
     */

    public Game game;

    boolean buildSuccessful = false;

    public Build(Game game) throws NotValidCellException, MapDimensionException {
        /* @constructor
         * it allows a player to build on a certain cell with a specific worker
         */

        this.game = game;

        // If the build is succesful, then the game proceed to change the turn
        game.setState(new ChangeTurn(game));

        // if the build isn't succesfull, the worker has to build again
        game.setState(new Build(game));

    }



    // maybe this isn't necessary (or goes into worker)
    private void noAllowedBuilds() {
        /* @function
         * if the player cannot build with any of his workers, he automatically loses
         */
    }

    private boolean cannotBuildWithWorker(Worker worker) {
        /* @function
         * if the player cannot build with the selected worker he is told so and can choose to move the other worker in order to build with him
         */
        // boolean cantBuild = false;

        return false;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}