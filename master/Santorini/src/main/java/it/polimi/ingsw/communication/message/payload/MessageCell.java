package it.polimi.ingsw.communication.message.payload;

public class MessageCell {
    private final int x;
    private final int y;

    MessageCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
