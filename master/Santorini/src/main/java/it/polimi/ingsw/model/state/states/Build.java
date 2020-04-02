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
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class Build implements GameState {
    /* @abstractClass
     * it represents the state where a player must (or can, in case of some God powers) build a block with his worker
     */

    public Game game;
    public Cell cellToBuildUp;

    public Build(Game game) {
        /* @constructor
         * it allows a player to build on a certain cell with a specific worker
         */

        this.game = game;

        // it shows the possible cells where the worker can build and then allows him to build on one of them
        game.getCurrentPlayer().getCurrentWorker().getPossibleBuilds();
        game.getCurrentPlayer().getCurrentWorker().build(cellToBuildUp);

        // if the build is successful, then the game proceed to change the turn
        if(isBuildSuccessful(game))
            game.setState(new ChangeTurn(game));
        else
        // if the build isn't successful, the worker has to build again
            game.setState(new Build(game));

    }

    private boolean isBuildSuccessful(Game game) {
        /* @predicate
         * it tells if a player picked a cell where he can actually build
         */
        // boolean buildSuccessful = false;

        return false;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}