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

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.map.Worker;
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class ChooseWorker implements GameState {
     /* @abstractClass
     * it represents the state where a player must choose the worker he wants to move
     */

    public Game game;
    public static Player curPlayer;

    public ChooseWorker(Game game) {
        /* @constructor
         * it lets the player choose the worker he wants to move
         */

        this.game = game;

        // if the current player chooses a worker that can move, the game goes to Move state
        game.setState(new Move(game));

        // otherwise he is told  to choose another worker
        game.setState(new ChooseWorker(game));

        // SE IL CURPLAYER NON HA MOSSE DISPONIBILI, VIENE ELIMINATO DAL GIOCO E VADO IN DEFEAT
        game.setState(new Defeat(game));

    }

    private boolean cannotMove(Player currentPlayer) {
        /*
         * it checks if the current player cannot move any of his workers
         */
        return false;
    }

    private boolean isWorkerMovable(Worker worker) {
        /* @predicate
         * it checks if the chosen worker can be moved, otherwise the player is told to try and pick the other worker
         */
        return true;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}