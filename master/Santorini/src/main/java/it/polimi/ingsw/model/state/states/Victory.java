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

public class Victory implements GameState {
    /* @abstractClass
     * it represents the state where a player wins
     */

    public Game game;

    public Victory(Game game) {
        /* @constructor
         * it shows the player that won and ends the game
         */

        this.game = game;

        Player winner = game.getCurrentPlayer();
        endGame(game);

        //Start a new game (if the players want to)
        // game.setState(new Start(game));
    }

    private void endGame(Game game) {
        /* @function
         * it shows the player that won and ends the current game
         */

        // once there is a winner, it shows the name of the winner
       // System.out.println("Congratulations" + game.getCurrentPlayer() + "! You are the winner!");

        // it cleans the whole board ( + probably needs to reset everything else?)
        game.board.clean();
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}