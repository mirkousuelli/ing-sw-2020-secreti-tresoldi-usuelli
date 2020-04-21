package it.polimi.ingsw.communication.message.xml.network.object;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;

public class AnswerXML<S> extends MessageXML<AnswerType, S> {

    private DemandType context;

    public AnswerXML(){}

    public DemandType getContext() {
        return context;
    }

    public void setContext(DemandType context) {
        this.context = context;
    }
}
