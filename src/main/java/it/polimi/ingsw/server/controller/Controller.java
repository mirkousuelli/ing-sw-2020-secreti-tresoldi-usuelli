package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.view.ActionToPerformView;

import java.util.logging.Logger;

/**
 * Class that represents the controller, which receives inputs from the user and then updates the model accordingly
 */
public class Controller implements Observer<ActionToPerformView> {

    private final Game model;
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    /**
     * Constructor of the controller
     * @param model the game which the controller is connected to
     */
    public Controller(Game model) {
        this.model = model;
    }

    /**
     * Class that performs the action required, updating the model accordingly.
     * If the player that tries to make an action cannot do it or isn't the current one, no action is performed: for
     * example if the players tries to make a move but he should choose the worker, he is notified with an error
     *
     * @param actionToPerformView parameter that connects the controller to the view of the player requiring the action
     */
    private synchronized void performAction(ActionToPerformView actionToPerformView) {
        if (!model.getCurrentPlayer().nickName.equals(actionToPerformView.getPlayer())) {
            actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, new ReducedMessage("Not current player")));
            LOGGER.info(() -> "Not current player!");
            return;
        }

        if (!actionToPerformView.getDemand().getHeader().equals(DemandType.USE_POWER)) {
            if (!model.getState().getName().equals(actionToPerformView.getDemand().getHeader().toString()) && !model.getState().getName().equals(State.ASK_ADDITIONAL_POWER.toString())) {
                actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, new ReducedMessage("Not permitted")));
                LOGGER.info(() -> "Not permitted!");
                return;
            }
        }

        model.setRequest(actionToPerformView);
        ReturnContent returnContent = model.gameEngine();

        if (returnContent == null || returnContent.getAnswerType().equals(AnswerType.ERROR)) {
            actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, new ReducedMessage("Error")));
            LOGGER.info(() -> "Error!");
        }
        else
            model.setState(returnContent.getState());
    }

    /**
     * Class that updates the game by performing the required action
     *
     * @param actionToPerformView parameter that connects the controller to the view of the player requiring the action
     */
    @Override
    public void update(ActionToPerformView actionToPerformView) {
        performAction(actionToPerformView);
    }
}