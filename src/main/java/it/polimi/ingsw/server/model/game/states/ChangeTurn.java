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

public class ChangeTurn implements GameState {
    /* @abstractClass
     * it represents the state where the current player changes and the win conditions are checked
     */

    public Game game;

    public ChangeTurn(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }


    private void changeCurrentPlayer(Game game) {
        /* @function
         * it switches the player that must play
         */

        if(game.getNumPlayers() == 3) {
            if (game.getCurrentPlayer() == game.getPlayerList().get(0))
                game.setCurrentPlayer(game.getPlayerList().get(1));
            else {
                if (game.getCurrentPlayer() == game.getPlayerList().get(1))
                    game.setCurrentPlayer(game.getPlayerList().get(2));
                else //which means that the current player is the last one
                    game.setCurrentPlayer(game.getPlayerList().get(0));
            }

        } else { // which means that there are two players in the game
            if (game.getCurrentPlayer() == game.getPlayerList().get(0))
                game.setCurrentPlayer(game.getPlayerList().get(1));
            else
                game.setCurrentPlayer(game.getPlayerList().get(0));
        }
    }


    private boolean onePlayerRemaining(Game game){
        return game.getNumPlayers() == 1;
    }


    // TODO actual check of win condition
    private boolean controlWinCondition(Game game) {
        /* @predicate
         * it checks if any win condition is verified (some God powers add a secondary win condition)
         */
        return false;
    }

    @Override
    public String getName() {
        return State.CHANGE_TURN.toString();
    }

    @Override
    public State gameEngine(Game game) {
        // Check if any win condition is verified (or if only one player remains); if so the game goes to Victory state
        if(controlWinCondition(game) || onePlayerRemaining(game))
            return State.VICTORY;
        else {
            // Otherwise the current player is changed and the game goes to ChooseWorker state
            changeCurrentPlayer(game);
            return State.CHOOSE_WORKER;
        }
    }
}