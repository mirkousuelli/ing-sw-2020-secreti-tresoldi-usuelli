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
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the state where the Challenger has to pick the list of cards which the game will be played with
 */
public class Start implements GameState {

    private final Game game;

    /**
     * Constructor of the state Start
     *
     * @param game the game which the state is connected to
     */
    public Start(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.START.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the challenger sends the list of Gods that will be used in the game. If the operation is successful
     * the state is set to ChooseCard, otherwise the state remains Start
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        //chooseDeck
        ReturnContent returnContent = new ReturnContent<>();

        //set challenger
        game.setCurrentPlayer(game.getPlayer(game.getRequest().getPlayer()));
        List<God> chosenGodList = ((List<God>) game.getRequest().getDemand().getPayload());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.START);

        //validate gods
        for (God g : chosenGodList) {
            if (God.parseString(g.toString()) == null)
                return returnContent;
        }

        game.setChosenGods(chosenGodList);

        returnContent.setAnswerType(AnswerType.SUCCESS);
        returnContent.setState(State.CHOOSE_CARD);
        returnContent.setPayload(game.getChosenGods().stream().map(ReducedCard::new).collect(Collectors.toList()));
        returnContent.setChangeTurn(true);

        return returnContent;
    }
}