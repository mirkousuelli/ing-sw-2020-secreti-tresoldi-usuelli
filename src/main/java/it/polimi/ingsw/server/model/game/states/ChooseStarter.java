package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import java.util.ArrayList;

/**
 * Class that represents the state where the starter is chosen
 */
public class ChooseStarter implements GameState {

    private final Game game;

    /**
     * Constructor of the state ChooseStarter
     *
     * @param game the game which the state is connected to
     */
    public ChooseStarter(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.CHOOSE_STARTER.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the starter is chosen and then the state is changed to ChooseStarter, where the starter has to place his
     * workers first
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent<>();

        String starter = ((ReducedMessage) game.getRequest().getDemand().getPayload()).getMessage();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_STARTER);

        for (Player p : game.getPlayerList()) {
            if (game.getCurrentPlayer().nickName.equals(starter)) {
                game.setStarter(game.getIndex(p));
                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.PLACE_WORKERS);
                returnContent.setChangeTurn(false);
                returnContent.setPayload(new ArrayList<>());

                break;
            }

            if (p.nickName.equals(starter)) {
                game.setStarter(game.getIndex(p));
                int newCur = (game.getStarter() + game.getNumPlayers() - 1) % game.getNumPlayers();
                game.setCurrentPlayer(game.getPlayer(newCur));
                returnContent.setChangeTurn(true);

                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.PLACE_WORKERS);
                returnContent.setPayload(new ArrayList<>());

                break;
            }
        }

        return returnContent;
    }
}
