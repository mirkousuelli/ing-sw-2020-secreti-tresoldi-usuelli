package it.polimi.ingsw.server.model;

import it.polimi.ingsw.communication.message.Demand;

/**
 * Class that represents the action that is required to be performed.
 * <p>
 * It contains the name of the player requesting it and the demand
 */
public class ActionToPerform<S> {

    private final String player;
    private final Demand<S> demand;

    /**
     * Constructor of the action to be performed
     *
     * @param player name of the player
     * @param demand the demand that is going to be performed
     */
    public ActionToPerform(String player, Demand<S> demand) {
        this.player = player;
        this.demand = demand;
    }

    public String getPlayer() {
        return player;
    }

    public Demand<S> getDemand() {
        return demand;
    }

}
