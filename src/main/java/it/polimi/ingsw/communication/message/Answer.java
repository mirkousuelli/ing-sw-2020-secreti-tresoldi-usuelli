package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

public class Answer<S> extends Message<AnswerType, S> {

    protected UpdatedPartType context;

    public Answer(AnswerType header, UpdatedPartType context, S payload) {
        super(header, payload);
        this.context = context;
    }

    public Answer(AnswerType header, UpdatedPartType context) {
        this(header, context, (S) new ReducedMessage("null"));
    }

    public Answer(AnswerType header) {
        this(header, null, (S) new ReducedMessage("null"));
    }

    public Answer(AnswerType header, S payload) {
        this(header, null, payload);
    }

    public Answer(Answer<S> msg) {
        super(msg.getHeader(), msg.getPayload());
        this.context = msg.getContext();
    }

    public Answer(){}

    public UpdatedPartType getContext() {
        return this.context;
    }

    public void setContext(UpdatedPartType context) {
        this.context = context;
    }
}
