package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.model.ActionToPerform;

/**
 * Class used by a remote view to notify the {@code Controller} a demand of the remove view's user.
 * It contains the demand itself and a way to do a callback to the remove view in case the execution of the demand went wrong.
 * In other words, this way consists of the instance of the remove view itself but wrapped as an {@code IView} to show only the methods to perform a callback
 */
public class ActionToPerformView<S> extends ActionToPerform<S> {

    private final IView iView;

    /**
     * Initializes an instance of {@code ActionToPerformView}
     *
     * @param demand the player's demand
     * @param player the player's name
     * @param iView  the player's remote view
     */
    public ActionToPerformView(String player, Demand<S> demand, IView iView) {
        super(player, demand);
        this.iView = iView;
    }

    public IView getIView() {
        return iView;
    }
}
