package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReduceDemandChoice;
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
            actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), new ReduceDemandChoice("Not current player")));
            return;
        }

        if (!model.getState().getName().equals(actionToPerformView.getDemand().getHeader().toString())) {
            actionToPerformView.getIView().reportError(new Answer<>(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), new ReduceDemandChoice("Not permitted")));
            return;
        }

        ReturnContent returnContent;
        model.setRequest(actionToPerformView);
        returnContent = model.gameEngine();

        if (returnContent == null || returnContent.getAnswerType().equals(AnswerType.ERROR)) {
            actionToPerformView.getIView().reportError(new Answer(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), new ReduceDemandChoice("Error")));
        }
        else
            model.setState(returnContent.getState());

    }

    @Override
    public void update(ActionToPerformView actionToPerformView) {
        performAction(actionToPerformView);
    }
}