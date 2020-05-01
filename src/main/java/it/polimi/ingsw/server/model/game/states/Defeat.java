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
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    // TODO
    private void removePlayer(Player currentPlayer) {
        /* @function
         * it eliminates the player (and his workers) that doesn't have any movable worker or cannot build with any of them
         */
    }


    @Override
    public String getName() {
        return State.DEFEAT.toString();
    }

    @Override
    public State gameEngine(Game game) {
        //the current player is eliminated with his workers
        removePlayer(game.getCurrentPlayer());
        game.setNumPlayers(game.getNumPlayers() - 1);

        // the game switches to ChangeTurn state
        return State.CHANGE_TURN;
    }

}