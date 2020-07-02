package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.UpdatedPartType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

/**
 * Class that represents the answer that the player receives after he sends a demand
 * <p>
 * For example if the player requires to make an irregular move, he receives an answer that contains an
 * {@link AnswerType} of {@code ERROR}
 * <p>
 * It extends {@link Message}
 *
 * @param <S> the payload containing the modified object
 */
public class Answer<S> extends Message<AnswerType, S> {

    protected UpdatedPartType context;

    /**
     * Constructor of the answer, given the header, the context and the payload
     *
     * @param header  the header of the answer
     * @param context the context representing what part has been updated
     * @param payload the payload of the answer
     */
    public Answer(AnswerType header, UpdatedPartType context, S payload) {
        super(header, payload);
        this.context = context;
    }

    /**
     * Constructor of the answer, given the header and the context
     *
     * @param header  the header of the answer
     * @param context the context representing what part has been updated
     */
    public Answer(AnswerType header, UpdatedPartType context) {
        this(header, context, (S) new ReducedMessage("null"));
    }

    /**
     * Constructor of the answer, given just the header
     *
     * @param header  the header of the answer
     */
    public Answer(AnswerType header) {
        this(header, null, (S) new ReducedMessage("null"));
    }

    /**
     * Constructor of the answer, given the header and the payload
     *
     * @param header  the header of the answer
     * @param payload payload of the answer
     */
    public Answer(AnswerType header, S payload) {
        this(header, null, payload);
    }

    /**
     * Constructor of the answer, given the message
     *
     * @param msg the message of the answer
     */
    public Answer(Answer<S> msg) {
        super(msg.getHeader(), msg.getPayload());
        this.context = msg.getContext();
    }

    public Answer() {
    }

    public UpdatedPartType getContext() {
        return this.context;
    }

    public void setContext(UpdatedPartType context) {
        this.context = context;
    }
}
