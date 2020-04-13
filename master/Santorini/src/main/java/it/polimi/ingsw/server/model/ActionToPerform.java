package it.polimi.ingsw.server.model;

import it.polimi.ingsw.communication.message.Demand;

public class ActionToPerform<S> {

    private final String player;
    private final Demand<S> demand;

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
