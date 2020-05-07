package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.MalusPower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
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
        God chosenGod = ((God) game.getRequest().getDemand().getPayload());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_CARD);

        for (God g : game.getChosenGods()) {
            if (g.equals(chosenGod)) {
                game.removeGod(g);
                game.getDeck().fetchCard(g);
                currentPlayer.setCard(game.getDeck().popCard(g));

                returnContent.setAnswerType(AnswerType.SUCCESS);
                if (game.getPlayer(0).getNickName().equals(game.getCurrentPlayer().getNickName()))
                    returnContent.setState(State.CHOOSE_STARTER);
                else
                    returnContent.setChangeTurn(true);

                returnContent.setPayload(new ReducedPlayer(currentPlayer.getNickName(), g));


                Power p = game.getCurrentPlayer().getCard().getPower(0);
                if (p.getEffect().equals(Effect.MALUS) && p.getTiming().equals(Timing.START_TURN)) {
                    ((MalusPower) p).usePower(game.getOpponents());
                }

                break;
            }
        }


        return returnContent;
    }
}
