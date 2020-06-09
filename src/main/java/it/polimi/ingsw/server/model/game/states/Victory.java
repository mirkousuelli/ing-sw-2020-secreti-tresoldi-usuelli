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

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

public class Victory implements GameState {
    /* @abstractClass
     * it means that the player that has entered the state has won the game
     */

    private final Game game;

    public Victory(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    private void endGame(Game game) {
        game.getBoard().clean(); // it cleans the whole board (and the pawns too)
    }

    @Override
    public String getName() {
        return State.VICTORY.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.SUCCESS);
        returnContent.setState(State.START);

        GameMemory.save(this, Lobby.backupPath);
        endGame(game);

        return returnContent;
    }
}