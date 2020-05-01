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
import it.polimi.ingsw.server.model.map.Level;

import java.util.List;

public class Move implements GameState {

    /* @abstractClass
     * it represents the state where a player has to move at least one of his worker
     */
    public Game game;
    private Cell cellToMoveTo;
    List<Cell> possibleMoves;

    public Move(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */
        this.game = game;
    }

    private boolean isMoveCorrect() {
        /* @predicate
         * it tells if the cell chosen by the player is a cell where he can actually move to
         */
        return possibleMoves.contains(cellToMoveTo);
    }

    private boolean reachedThirdLevel(Game game) {
        /* @predicate
         * it tells if a worker reached the third level
         */
        return game.getCurrentPlayer().getCurrentWorker().getLevel() == Level.TOP;
    }


    @Override
    public String getName() {
        return State.MOVE.toString();
    }

    @Override
    public State gameEngine(Game game) {
        possibleMoves = game.getBoard().getPossibleMoves(game.getCurrentPlayer());
        // if the curPlayer cannot move with the chosen worker, he gets to choose a different one and the game goes to ChooseWorker state
        if (!game.getCurrentPlayer().getCurrentWorker().isMovable())
            return State.CHOOSE_WORKER;
        else { // if the worker can be moved, the player is showed the cells he can move to and moves to one of them
            // System.out.println(possibleMoves);
            // it checks if the chosen cell is in the possible moves, otherwise the player has to move again
            if(!isMoveCorrect())
                return State.MOVE;
            else {
                game.getBoard().move(game.getCurrentPlayer(), cellToMoveTo);
                //if the worker is moved to a third level (from a second one), the player that moved wins
                if (reachedThirdLevel(game))
                    return State.VICTORY;
                else //which means that no one won in this turn, the game switches to Build state
                    return State.BUILD;
            }
        }
    }
}