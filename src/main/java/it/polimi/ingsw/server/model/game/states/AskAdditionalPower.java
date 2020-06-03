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

public class AskAdditionalPower implements GameState {

    private final Game game;

    public AskAdditionalPower(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.ASK_ADDITIONAL_POWER.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent();

        ReducedMessage response = (ReducedMessage) game.getRequest().getDemand().getPayload();
        State prevState = game.getPrevState();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.ASK_ADDITIONAL_POWER);

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
        }
        else if (response.getMessage().equals("y")) { //else, it has to be that he wants to use his additional power, so go to additional power!
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.ADDITIONAL_POWER);

            Effect effect = game.getCurrentPlayer().getCard().getPower(0).getEffect();
            Power p = game.getCurrentPlayer().getCard().getPower(0);

            if (effect.equals(Effect.BUILD) && p.getTiming().equals(Timing.ADDITIONAL)) //if it's an additional build power
                returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.ADDITIONAL, State.BUILD));
            else if (effect.equals(Effect.MOVE) && p.getTiming().equals(Timing.ADDITIONAL)) //if it's an additional move power
                returnContent.setPayload(PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER));
        }
        else if (response.getMessage().equals("yes"))
            returnContent.setState(State.ADDITIONAL_POWER);

        return returnContent;
    }
}
