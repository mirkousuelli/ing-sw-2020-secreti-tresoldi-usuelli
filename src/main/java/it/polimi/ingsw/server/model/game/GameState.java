/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.game;

/**
 * Interface that represents the states which the game is divided into
 * <p>
 * The methods of this class are implemented by each single state, working differently depending on which is
 * the current state
 */
public interface GameState {

    String getName();

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    ReturnContent gameEngine();
}