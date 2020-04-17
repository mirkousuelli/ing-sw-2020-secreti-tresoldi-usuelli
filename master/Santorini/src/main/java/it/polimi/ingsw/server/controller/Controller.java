package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.model.state.Game;
import it.polimi.ingsw.server.view.ActionToPerformView;

public class Controller implements Observer<ActionToPerformView> {

    private final Game model;

    public Controller(Game model) {
        this.model = model;
    }

    private synchronized void performAction(ActionToPerformView actionToPerformView) {
        if (!model.getCurrentPlayer().nickName.equals(actionToPerformView.getPlayer())) {
            actionToPerformView.getIView().reportError(new Answer<String>(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), "Not current player"));
            return;
        }

        if (!model.getState().toString().equals(actionToPerformView.getDemand().getHeader().toString())) {
            actionToPerformView.getIView().reportError(new Answer<String>(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), "Not permitted"));
            return;
        }

        model.setRequest(actionToPerformView);
        /*Object ret = */model.gameEngine();

        /*if (ret != null)
            actionToPerformView.getIView().reportError(new Answer(AnswerType.ERROR, (DemandType) actionToPerformView.getDemand().getHeader(), ret));
        */
    }

    @Override
    public void update(ActionToPerformView actionToPerformView) {
        performAction(actionToPerformView);
    }
}