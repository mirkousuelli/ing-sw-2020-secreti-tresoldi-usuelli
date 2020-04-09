package it.polimi.ingsw.communication.message;

public class Demand<S> extends Message<DemandType, S> {

    public Demand(DemandType header, S payload) {
        super(header, payload);
    }

}
