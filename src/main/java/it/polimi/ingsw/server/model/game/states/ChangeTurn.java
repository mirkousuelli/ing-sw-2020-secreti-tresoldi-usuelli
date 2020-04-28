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

import it.polimi.ingsw.client.view.cli.Color;
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
         * it changes the turn and the new player has to move one of his worker
         */

        this.game = game;

        // Check if any win condition is verified (or if only one player remains; if so the game goes to Victory state
        if(controlWinCondition(game) || onePlayerRemaining(game))
            game.setState(new Victory(game));
         else {
             // Otherwise the current player is changed and the game goes to ChooseWorker state
            changeCurrentPlayer(game);
            game.setState(new ChooseWorker(game));
        }
    }

    private void changeCurrentPlayer(Game game) {
        /* @function
         * it switches the player that must play
         */
        Player p1 = new Player(game.getPlayer(0).getNickName());
        Player p2 = new Player(game.getPlayer(1).getNickName());


        if (game.getNumPlayers() == 3) {
            Player p3 = new Player(game.getPlayer(2).getNickName());

            if (game.getCurrentPlayer().nickName.equals(game.getPlayer(0).getNickName())) {
                game.setCurrentPlayer(p2);
            } else if (game.getCurrentPlayer().nickName.equals(game.getPlayer(1).getNickName())) {
                game.setCurrentPlayer(p3);
            } else if (game.getCurrentPlayer().nickName.equals(game.getPlayer(2).getNickName())) {
                game.setCurrentPlayer(p1);
            }
        } else { //which means that there are 2 players remaining
            if (game.getCurrentPlayer().nickName.equals(game.getPlayer(0).getNickName())) {
                game.setCurrentPlayer(p2);
            } else if (game.getCurrentPlayer().nickName.equals(game.getPlayer(1).getNickName())) {
                game.setCurrentPlayer(p1);
            }

        }
    }

    private boolean onePlayerRemaining(Game game){
        return game.getNumPlayers() == 1;
    }

    private boolean controlWinCondition(Game game) {
        /* @predicate
         * it checks if any win condition is verified (some God powers add a secondary win condition)
         */
        return false;
    }


    public String getName() {
        return State.CHANGE_TURN.toString();
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}