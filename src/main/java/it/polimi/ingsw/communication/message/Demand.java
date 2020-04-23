package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.DemandType;

public class Demand<S> extends Message<DemandType, S> {

    public Demand(DemandType header, S payload) {
        super(header, payload);
    }

    public Demand(Demand<S> msg) {
        super(msg.getHeader(), msg.getPayload());
    }

    public Demand(){}

}
