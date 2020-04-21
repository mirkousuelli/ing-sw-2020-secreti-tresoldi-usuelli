package it.polimi.ingsw.communication.message.payload;

public class MessageCell {

    private final int x;
    private final int y;

    public MessageCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
