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
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import java.util.List;

public class Start implements GameState {
    /* @Class
     * it represents the state that includes all the actions to initialize the game
     */

    private final Game game;

    public Start(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    @Override
    public String getName() {
        return State.START.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        //chooseDeck
        ReturnContent returnContent = new ReturnContent();

        //set challenger
        Player currentPlayer = game.getPlayer(0);
        game.setCurrentPlayer(currentPlayer);
        List<God> chosenGodList = ((List<God>) game.getRequest().getDemand().getPayload());

        game.setChoosenGods(chosenGodList);

        returnContent.setAnswerType(AnswerType.SUCCESS);
        returnContent.setState(State.CHOOSE_CARD);
        returnContent.setPayload(chosenGodList);
        returnContent.setChangeTurn(true);

        return returnContent;
    }
}