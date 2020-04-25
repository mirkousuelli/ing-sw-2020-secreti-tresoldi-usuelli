/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;

import java.util.List;

public class Build implements GameState {
    /* @abstractClass
     * it represents the state where a player must (or can, in case of some God powers) build a block with his worker
     */

    public Game game;
    private Cell currentCell;
    private Cell cellToBuildUp;
    List<Cell> possibleBuilds = game.getBoard().getPossibleBuilds(currentCell);

    public Build(Game game) {
        /* @constructor
         * it allows a player to build on a certain cell with a specific worker
         */

        this.game = game;
        // it shows the possible cells where the worker can build and then allows him to build on one of them
        // System.out.println(possibleBuilds);
        game.getBoard().build(game.getCurrentPlayer(), cellToBuildUp);

        // this.game.getBoard().build(game.getCurrentPlayer().getCurrentWorker(), cellToBuildUp);

        // if the player builds on a possible cell, then the game proceed to change the turn
        if(isBuildSuccessful(game))
            game.setState(new ChangeTurn(game));
        else
        // if the player selects a cell where he cannot build,he has to build again
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