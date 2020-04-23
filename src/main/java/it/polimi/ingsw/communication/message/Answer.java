package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;

public class Answer<S> extends Message<AnswerType, S> {

    protected DemandType context;

    public Answer(AnswerType header, DemandType context, S payload) {
        super(header, payload);
        this.context = context;
    }

    public Answer(Answer<S> msg) {
        super(msg.getHeader(), msg.getPayload());
        this.context = msg.getContext();
    }

    public Answer(){}

    public DemandType getContext() {
        return this.context;
    }

    public void setContext(DemandType context) {
        this.context = context;
    }
}
