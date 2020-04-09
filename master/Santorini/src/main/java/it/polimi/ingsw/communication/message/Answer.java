package it.polimi.ingsw.communication.message;

public class Answer<S> extends Message<AnswerType, S> {

    protected final DemandType context;

    public Answer(AnswerType header, DemandType context, S payload) {
        super(header, payload);
        this.context = context;
    }

    public DemandType getContext() {
        return this.context;
    }

}
