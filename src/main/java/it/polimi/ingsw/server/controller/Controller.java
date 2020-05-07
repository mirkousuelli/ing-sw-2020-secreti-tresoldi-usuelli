package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.view.ActionToPerformView;

public class Controller implements Observer<ActionToPerformView> {

    private final Game model;

    public Controller(Game model) {
        this.model = model;
    }

    private synchronized void performAction(ActionToPerformView actionToPerformView) {
        if (!model.getCurrentPlayer().nickName.equals(actionToPerformView.getPlayer())) {
            actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), new ReducedMessage("Not current player")));
            return;
        }

        if (!actionToPerformView.getDemand().getHeader().equals(DemandType.USE_POWER)) {
            if (!model.getState().getName().equals(actionToPerformView.getDemand().getHeader().toString())) {
                actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), new ReducedMessage("Not permitted")));
                return;
            }
        }

        model.setRequest(actionToPerformView);
        ReturnContent returnContent = model.gameEngine();

        if (returnContent == null || returnContent.getAnswerType().equals(AnswerType.ERROR)) {
            actionToPerformView.getIView().reportError(new Answer(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), new ReducedMessage("Error")));
        }
        else
            model.setState(returnContent.getState());

    }

    @Override
    public void update(ActionToPerformView actionToPerformView) {
        performAction(actionToPerformView);
    }
}