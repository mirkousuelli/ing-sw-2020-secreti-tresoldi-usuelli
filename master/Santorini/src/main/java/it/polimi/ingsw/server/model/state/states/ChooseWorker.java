/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.state.states;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.state.Game;
import it.polimi.ingsw.server.model.state.GameState;

public class ChooseWorker implements GameState {
     /* @abstractClass
     * it represents the state where a player must choose the worker he wants to move
     */

    public Game game;

    public ChooseWorker(Game game) {
        /* @constructor
         * it lets the player choose the worker he wants to move
         */

        this.game = game;

        // if currentPlayer cannot move any of his workers, the game switches to Defeat state (he loses)
        if(cannotMove(game.getCurrentPlayer()))
            game.setState(new Defeat(game));
        else {
            // the player has to pick a worker and the game goes to Move state
            // + ACTUAL PICK OF THE WORKER
            game.setState(new Move(game));
        }
    }

    private boolean cannotMove(Player currentPlayer) {
        /* @predicate
         * it checks if the current player cannot move any of his workers
         */
        return false;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}