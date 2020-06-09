package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.MalusPower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the state where each player has to choose the card he wants to use
 */
public class ChooseCard implements GameState {

    private final Game game;

    /**
     * Constructor of the state ChooseCard
     *
     * @param game the game which the state is connected to
     */
    public ChooseCard(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.CHOOSE_CARD.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here every player has to pick the card he wants to use (from the deck containing the cards picked by the
     * Challenger. The game is then set to ChooseStarter.
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent<>();

        Player currentPlayer = game.getCurrentPlayer();
        God chosenGod = ((God) game.getRequest().getDemand().getPayload());
        Card chosenCard = null;

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_CARD);

        for (Card g : game.getChosenGods()) {
            if (g.getGod().equals(chosenGod)) {
                chosenCard = g;
                break;
            }
        }

        if (chosenCard != null) {
            List<ReducedCard> toReturn = new ArrayList<>();

            game.removeGod(chosenCard);
            currentPlayer.setCard(chosenCard);

            returnContent.setAnswerType(AnswerType.SUCCESS);
            if (game.getPlayer(0).getNickName().equals(game.getCurrentPlayer().getNickName()))
                returnContent.setState(State.CHOOSE_STARTER);
            else
                returnContent.setChangeTurn(true);

            toReturn.add(new ReducedCard(chosenCard));
            returnContent.setPayload(toReturn);

            ChooseCard.applyMalus(game, Timing.DEFAULT);
        }

        return returnContent;
    }

    /**
     * Method that applies a malus, with the correct timing
     * <p>
     * It is used at the beginning setting the timing to default and after some specific moves in the move state, where
     * the timing can be end_turn
     * <p>
     * This method is static since can be used in other classes
     *
     * @param game the game that is being played, where to apply the malus
     * @param timing timing of the malus
     */
    public static void applyMalus(Game game, Timing timing) {
        Power p = game.getCurrentPlayer().getCard().getPower(0);

        if (p.getEffect().equals(Effect.MALUS) && p.getTiming().equals(timing)) {
            ((MalusPower) p).usePower(game.getOpponents(), game.getCurrentPlayer());
        }
    }
}
