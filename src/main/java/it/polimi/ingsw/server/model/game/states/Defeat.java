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

public class Defeat implements GameState {
    /* @abstractClass
     * it represents the state where a player has lost
     */
    public Game game;

    public Defeat(Game game) {
        /* @constructor
         * it tells a player that he lost and then eliminates him
         */

        this.game = game;

        //the current player is eliminated with his workers
        removePlayer(game.getCurrentPlayer());
        removeWorkers(game.getCurrentPlayer());
        game.setNumPlayers(game.getNumPlayers() - 1);

        // the game switches to ChangeTurn state (or maybe we could change here the current player and then go directly to ChooseWorker)
        game.setState(new ChangeTurn(game));

    }

    private void removePlayer(Player currentPlayer) {
        /* @function
         * it eliminates the player that doesn't have any movable worker or cannot move with any of them
         */
    }

    private void removeWorkers(Player currentPlayer) {
        /* @function
         * it removes the workers of a player that lost
         */
    }

    @Override
    public String getName() {
        return State.DEFEAT.toString();
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }

}