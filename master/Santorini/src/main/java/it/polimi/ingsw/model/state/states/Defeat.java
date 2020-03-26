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
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class Defeat implements GameState {
    /* @abstractClass
     * it represents the state where a player has lost
     */

    public Game game;
    public Player currentPlayer;

    public Defeat(Game game) {
        /* @constructor
         * it tells a player that he lost and then eliminates him
         */

        this.game = game;

        //the current player is eliminated
        currentPlayer = game.getCurrentPlayer();
        removePlayer(currentPlayer);
        game.numPlayerRemaining--;

        // the game switches to ChangeTurn state
        game.setState(new ChangeTurn(game));

    }

    private void removePlayer(Player currentPlayer) {
        /* @function
         * it eliminates the player that doesn't have any movable worker or cannot move with any of them
         */
    }

    private void removeWorker(Game game) {
        /* @function
         * it eliminates a worker that is chosen as target from God powers that can do this
         */
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }

}