package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.communication.message.header.AnswerType;

/**
 * Class that represents the content that is returned after every gameEngine
 * <p>
 *      <ol> It contains:
 *          <li> answerType, which is the outcome of the action
 *          <li> payload, containing the element used to make each action (for example if the action is a build, the payload is the reducedDemandCell that corresponds to the cell where the player decided to build)
 *          <li> state, the State that the game changed to after the action are made
 *          <li> changeTurn, that tells when the current player is changed
 *          <li> availableGods, that tells when the list of Gods is available
 *      </ol>
 *
 * @param <S> the generic parameter of this class
 */
public class ReturnContent<S> {

    private AnswerType answerType;
    private S payload;
    private State state;
    private boolean changeTurn;

    /**
     * Constructor of the returnContent, initialising all its attributes to null/false
     */
    public ReturnContent() {
        this(null, null, null, false);
    }

    /**
     * Constructor of the returnContent
     *
     * @param answerType the outcome of the action
     * @param payload the element used to make the action
     * @param state the state that the game changed to, after the action are made
     */
    public ReturnContent(AnswerType answerType, S payload, State state) {
        this(answerType, payload, state, false);
    }

    /**
     * Constructor of the returnContent
     *
     * @param answerType the outcome of the action
     * @param payload the element used to make the action
     * @param state the state that the game changed to, after the action are made
     * @param changeTurn tells when the current player changes
     */
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

    /**
     * Method that tells if the game changes the current player
     *
     * @return {@code true} if the current player changes, {@code false} otherwise
     */
    public boolean isChangeTurn() {
        return changeTurn;
    }

    public void setChangeTurn(boolean changeTurn) {
        this.changeTurn = changeTurn;
    }
}
