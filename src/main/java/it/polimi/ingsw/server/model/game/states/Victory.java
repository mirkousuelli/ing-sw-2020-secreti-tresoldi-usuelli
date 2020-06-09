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

/**
 * Class that represents the state where the player that enters is the winner
 */
public class Victory implements GameState {

    private final Game game;

    /**
     * Constructor of the state Victory
     *
     * @param game the game which the state is connected to
     */
    public Victory(Game game) {
        this.game = game;
    }

    /**
     * Method that cleans the board, deleting the pawns too
     *
     * @param game the game which the board is cleaned
     */
    private void endGame(Game game) {
        game.getBoard().clean();
    }

    @Override
    public String getName() {
        return State.VICTORY.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the board is cleaned and the state is set back to Start, allowing a new game to be played
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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