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

import it.polimi.ingsw.server.model.game.State;
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
    private Cell chosenCell;
    List<Cell> possibleBuilds;

    public Build(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */
        this.game = game;
    }

    private boolean isBuildPossible() {
        /* @predicate
         * it tells if a player picked a cell where he can actually build
         */
        return possibleBuilds.contains(chosenCell);
    }

    @Override
    public String getName() {
        return State.BUILD.toString();
    }

    @Override
    public State gameEngine(Game game) {
        possibleBuilds = game.getBoard().getPossibleBuilds(currentCell);
        // it shows the possible cells where the player can build and then allows him to choose one
        // System.out.println(possibleBuilds);
        game.getBoard().build(game.getCurrentPlayer(), chosenCell);

        // if the player chose a possible cell, the game actually builds on it and then proceed to change the turn
        if(isBuildPossible()) {
            game.getBoard().build(game.getCurrentPlayer(), chosenCell);
            return State.CHANGE_TURN;
        } else
            // if the player selects a cell where he cannot build, he has to build again
            return State.BUILD;
    }
}