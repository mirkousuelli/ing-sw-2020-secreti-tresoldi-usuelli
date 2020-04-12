package it.polimi.ingsw.communication.message.xml;

import it.polimi.ingsw.communication.message.AnswerType;
import it.polimi.ingsw.communication.message.DemandType;

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
