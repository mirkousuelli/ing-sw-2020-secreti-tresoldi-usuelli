package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.communication.message.header.AnswerType;

public class ReturnContent<S> {

    private AnswerType answerType;
    private S payload;
    private State state;
    private boolean changeTurn;
    private boolean availableGods;

    public ReturnContent() {
        this(null, null, null, false, false);
    }

    public ReturnContent(AnswerType answerType, S payload, State state) {
        this(answerType, payload, state, false, false);
    }

    public ReturnContent(AnswerType answerType, S payload, State state, boolean changeTurn) {
        this(answerType, payload, state, changeTurn, false);
    }

    public ReturnContent(AnswerType answerType, S payload, State state, boolean changeTurn, boolean availableGods) {
        this.answerType = answerType;
        this.payload = payload;
        this.state = state;
        this.changeTurn = changeTurn;
        this.availableGods = availableGods;
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

    public boolean isAvailableGods() {
        return availableGods;
    }

    public void setAvailableGods(boolean availableGods) {
        this.availableGods = availableGods;
    }
}
