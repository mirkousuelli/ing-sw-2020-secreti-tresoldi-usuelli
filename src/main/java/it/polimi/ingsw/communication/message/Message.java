package it.polimi.ingsw.communication.message;

/**
 * Abstract class that represents the message that is sent and can be a demand or an answer
 * <p>
 * It is developed deeper by its subclasses {@link Answer} and {@link Demand}
 *
 * @param <H> the header that contains the type of answer or demand that is sent with the message
 * @param <P> the payload that contains the reduced object that is sent (or received) in the message
 */
public abstract class Message<H, P> {

    private H header;
    private P payload;

    /**
     * Constructor of the message, which contains a header and a payload
     *
     * @param header  the header of the message
     * @param payload the payload of the message
     */
    public Message(H header, P payload) {
        this.header = header;
        this.payload = payload;
    }

    public Message() {
    }

    public H getHeader() {
        return this.header;
    }

    public P getPayload() {
        return this.payload;
    }

    public void setHeader(H header) {
        this.header = header;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }

}
