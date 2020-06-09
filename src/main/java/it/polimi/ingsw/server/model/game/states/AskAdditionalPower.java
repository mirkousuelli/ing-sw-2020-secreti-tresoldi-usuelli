package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import java.util.ArrayList;

/**
 * Class that represents the state where a player is asked if he wants to use an eventual additional power
 */
public class AskAdditionalPower implements GameState {

    private final Game game;

    /**
     * Constructor of the state AskAdditionalPower
     *
     * @param game the game which the state is connected to
     */
    public AskAdditionalPower(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.ASK_ADDITIONAL_POWER.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the player is asked if he wants to use an eventual additional power and the state is set depending on
     * what the player chooses
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.ASK_ADDITIONAL_POWER);

        if (game.getRequest().getDemand().getHeader().equals(DemandType.ASK_ADDITIONAL_POWER))
            returnContent = ask();
        else if (game.getRequest().getDemand().getHeader().equals(DemandType.ADDITIONAL_POWER))
            returnContent = new AdditionalPower(game).gameEngine();
        else if (game.getRequest().getDemand().getHeader().equals(DemandType.BUILD))
            returnContent = new Build(game).gameEngine();

        return returnContent;
    }

    /**
     * Method that asks the player if he wants to use the additional power. The state then changes depending on the
     * message sent by the player
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent ask() {
        ReturnContent returnContent = new ReturnContent<>();

        ReducedMessage response = (ReducedMessage) game.getRequest().getDemand().getPayload();
        State prevState = game.getPrevState();

        if (response.getMessage().equals("n")) { //if the current player does not want to use his additional power
            returnContent.setAnswerType(AnswerType.SUCCESS);
            if (prevState.equals(State.MOVE)) { //then if it's an additional move power
                returnContent.setState(State.BUILD); //then go to build
                returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
            } else if (prevState.equals(State.BUILD)) { //then if it's an additional build power
                returnContent.setState(State.CHOOSE_WORKER); //then go to choose worker, end  the current turn and start a new one
                returnContent.setChangeTurn(true);
                returnContent.setPayload(new ArrayList<ReducedAnswerCell>());
            }
        } else if (response.getMessage().equals("y")) { //else, it has to be that he wants to use his additional power, so go to additional power!
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.ADDITIONAL_POWER);

            Effect effect = game.getCurrentPlayer().getCard().getPower(0).getEffect();
            Power p = game.getCurrentPlayer().getCard().getPower(0);

            if (effect.equals(Effect.BUILD) && p.getTiming().equals(Timing.ADDITIONAL)) //if it's an additional build power
                returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.ADDITIONAL, State.BUILD));
            else if (effect.equals(Effect.MOVE) && p.getTiming().equals(Timing.ADDITIONAL)) //if it's an additional move power
                returnContent.setPayload(PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER));
        }

        return returnContent;
    }
}
