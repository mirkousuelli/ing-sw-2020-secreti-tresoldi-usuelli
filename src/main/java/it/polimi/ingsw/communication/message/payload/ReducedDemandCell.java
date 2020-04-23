package it.polimi.ingsw.communication.message.payload;

public class ReducedDemandCell {

    private int x;
    private int y;

    public ReducedDemandCell(){}

    public ReducedDemandCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
