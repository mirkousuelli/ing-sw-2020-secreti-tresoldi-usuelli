package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

public class ChooseStarter implements GameState {
    private final Game game;

    public ChooseStarter(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    @Override
    public String getName() {
        return "chooseStarter";
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent();

        ReduceDemandChoice starter = ((ReduceDemandChoice) game.getRequest().getDemand().getPayload());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_STARTER);

        for (Player p : game.getPlayerList()) {
            if(p.nickName.equals(starter.getChoice())) {
                game.setCurrentPlayer(p);

                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.PLACE_WORKERS);
                returnContent.setPayload(p.nickName);
            }
        }

        return returnContent;
    }
}
