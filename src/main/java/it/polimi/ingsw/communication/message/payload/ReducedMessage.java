package it.polimi.ingsw.communication.message.payload;

public class ReducedMessage {

    private String message;

    public ReducedMessage() {}

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
