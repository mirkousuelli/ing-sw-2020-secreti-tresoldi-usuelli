package it.polimi.ingsw.communication.message.payload;

/**
 * Class that represents the reduced version of a message, which contains as attribute a string which is the actual
 * message
 */
public class ReducedMessage {

    private String message;

    public ReducedMessage() {}

    /**
     * Constructor of the reduced message, that is created starting from the string of the message
     *
     * @param message string that represents the message
     */
    public ReducedMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
