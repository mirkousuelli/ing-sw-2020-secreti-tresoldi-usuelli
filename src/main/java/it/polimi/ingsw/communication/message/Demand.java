package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.network.object.DemandXML;

public class Demand<S> extends Message<DemandType, S> {

    public Demand(DemandType header, S payload) {
        super(header, payload);
    }

    public Demand(DemandXML<S> xml) {
        super(xml.getHeader(), xml.getPayload());
    }

    @Override
    public DemandXML<S> messageToXML() {

        DemandXML<S> toSend = new DemandXML<>();

        toSend.setHeader(this.getHeader());
        toSend.setPayload(this.getPayload());

        return toSend;

    }

}
