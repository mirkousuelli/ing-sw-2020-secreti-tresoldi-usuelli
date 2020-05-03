package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

public class Answer<S> extends Message<AnswerType, S> {

    protected DemandType context;

    public Answer(AnswerType header, DemandType context, S payload) {
        super(header, payload);
        this.context = context;
    }

    public Answer(AnswerType header, DemandType context) {
        this(header, context, (S) new ReducedMessage("null"));
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
