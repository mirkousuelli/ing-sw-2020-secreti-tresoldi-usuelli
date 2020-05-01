package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.communication.message.header.AnswerType;

public class ReturnContent<S> {

    private AnswerType answerType;
    private S payload;
    private State state;

    public ReturnContent() {}

    public AnswerType getAnswerType() {
        return answerType;
    }

    public S getPayload() {
        return payload;
    }

    public State getState() {
        return state;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public void setPayload(S payload) {
        this.payload = payload;
    }

    public void setState(State state) {
        this.state = state;
    }
}
