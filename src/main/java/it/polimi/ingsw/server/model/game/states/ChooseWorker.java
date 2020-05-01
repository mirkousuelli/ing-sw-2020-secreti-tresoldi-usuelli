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

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.State;

public class ChooseWorker implements GameState {
     /* @abstractClass
     * it represents the state where a player must choose the worker he wants to move
     */
    public Game game;

    public ChooseWorker(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    private boolean cannotMove(Player currentPlayer) {
        /* @predicate
         * it checks if the current player cannot move any of his workers
         */
        return (!game.getCurrentPlayer().getWorkers().get(0).isMovable() && !game.getCurrentPlayer().getWorkers().get(1).isMovable());
    }

    @Override
    public String getName() {
        return State.CHOOSE_WORKER.toString();
    }

    @Override
    public State gameEngine(Game game) {
        /*
         *
         */

        // if currentPlayer cannot move any of his workers, the game switches to Defeat state (he loses)
        if(cannotMove(game.getCurrentPlayer()))
            return State.DEFEAT;
        else {
            // the player has to pick a worker and the game goes to Move state
            // TODO actual pick of the worker

            /*it depends if we want to allow the player to try and move even if he chose a worker that cannot move
            (in this case he is blocked in Move state and he goes back to choose a worker). */
          //  if(game.getCurrentPlayer().getCurrentWorker().isMovable()) {
                // System.out.println("The worker you chose cannot move, pick a different one");}
            return State.MOVE;
        }
    }
}