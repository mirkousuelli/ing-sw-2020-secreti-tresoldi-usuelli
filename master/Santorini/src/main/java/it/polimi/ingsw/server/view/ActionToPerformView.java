package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.model.ActionToPerform;

public class ActionToPerformView<S> extends ActionToPerform<S> {

    private final IView iView;

    public ActionToPerformView(String player, Demand<S> demand, IView iView) {
        super(player, demand);
        this.iView = iView;
    }

    public IView getIView() {
        return iView;
    }
}
