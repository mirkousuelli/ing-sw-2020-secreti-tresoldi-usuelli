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

public class Move implements GameState {

    /* @abstractClass
     * it represents the state where a player has to move at least one of his worker
     */
    public Game game;
    private Cell currentCell;
    private Cell cellToMoveTo;
    //List<Cell> possibleMoves = game.getBoard().getPossibleMoves(currentCell);

    public Move(Game game) {
        /* @constructor
         * it allows a player to move to a certain cell with a specific worker
         */

        /*this.game = game;

        // if the curPlayer cannot move with the chosen worker, he gets to choose a different one and the game goes to ChooseWorker state
        // ...
        if (!game.getCurrentPlayer().getCurrentWorker().isMovable()) {
            game.setState(new ChooseWorker(game));
        } else { // if the worker can be moved, the player is showed the cells he can move to and moves to one of them
           // System.out.println(possibleMoves);
            game.getBoard().move(game.getCurrentPlayer(),cellToMoveTo);
            // if the worker is moved to a third level (from a second one), the player that moved wins
            // ...
            if (reachedThirdLevel(game))
                game.setState(new Victory(game));
            else //if no one won in this turn
                // if the move is made properly, the game switches to Build state
                if (isMoveCorrect = true)
                    game.setState(new Build(game));
                else
                    // if the move isn't possible, the player has to move again
                    game.setState(new Move(game));
        }*/
    }

    private boolean isMoveCorrect = true;

    private boolean reachedThirdLevel(Game game) {
        /* @predicate
         * it tells if a worker reached the third level
         */
        return false;
    }

    private boolean movedWorker(Game game) {
        /* @predicate
         * it tells whether or not the player moved at least a worker in this turn
         */
        return true;
    }

    private void removeWorker(Game game) {
        /* @function
         * it removes a worker from the game because of some God Power that can do it
         */
    }

    @Override
    public String getName() {
        return State.MOVE.toString();
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}