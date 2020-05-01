package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

public class ChooseCard implements GameState {

    private final Game game;

    public ChooseCard(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return "chooseCard";
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent();

        Player currentPlayer = game.getCurrentPlayer();
        God choosenGod = ((God) game.getRequest().getDemand().getPayload());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_CARD);

        for (God g : game.getChoosenGods()) {
            if (g.equals(choosenGod)) {
                game.removeGod(g);
                currentPlayer.setCard(game.getDeck().popCard(g));

                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.CHANGE_TURN);
                returnContent.setPayload(game.getChoosenGods());
            }
        }


        return returnContent;
    }
}
