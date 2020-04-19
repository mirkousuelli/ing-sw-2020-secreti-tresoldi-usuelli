package it.polimi.ingsw.communication.message.payload;

public class MessageCell {

    private final int x;
    private final int y;
    private final String color;

    public MessageCell(int x, int y) {
        this.x = x;
        this.y = y;
        color = null;
    }

    public MessageCell(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getColor() {
        return color;
    }
}
