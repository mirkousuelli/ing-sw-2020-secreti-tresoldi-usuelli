package it.polimi.ingsw.communication.message.payload;

public class ReducedMessage {

    private String message;
    private String color;

    public ReducedMessage() {}

    public ReducedMessage(String message) {
        this(message, "");
    }

    public ReducedMessage(String message, String color) {
        this.message = message;
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
