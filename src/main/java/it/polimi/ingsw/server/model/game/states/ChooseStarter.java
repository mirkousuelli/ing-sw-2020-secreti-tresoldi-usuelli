package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import java.util.ArrayList;

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

        String starter = ((ReducedMessage) game.getRequest().getDemand().getPayload()).getMessage();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_STARTER);

        for (Player p : game.getPlayerList()) {
            if(p.nickName.equals(starter)) {
                if (!p.nickName.equals(game.getCurrentPlayer().nickName)) {
                    game.setStarter(game.getIndex(p));
                    game.setCurrentPlayer(game.getPlayer(game.getStarter() - 1));
                    returnContent.setChangeTurn(true);
                }

                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.PLACE_WORKERS);
                returnContent.setPayload(new ArrayList<>());

                break;
            }
        }

        return returnContent;
    }
}
