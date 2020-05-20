package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.communication.message.header.AnswerType;

public class ReturnContent<S> {

    private AnswerType answerType;
    private S payload;
    private State state;
    private boolean changeTurn;

    public ReturnContent() {
        this(null, null, null, false);
    }

    public ReturnContent(AnswerType answerType, S payload, State state) {
        this(answerType, payload, state, false);
    }

    public ReturnContent(AnswerType answerType, S payload, State state, boolean changeTurn) {
        this.answerType = answerType;
        this.payload = payload;
        this.state = state;
        this.changeTurn = changeTurn;
    }

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

    public boolean isChangeTurn() {
        return changeTurn;
    }

    public void setChangeTurn(boolean changeTurn) {
        this.changeTurn = changeTurn;
    }
}
