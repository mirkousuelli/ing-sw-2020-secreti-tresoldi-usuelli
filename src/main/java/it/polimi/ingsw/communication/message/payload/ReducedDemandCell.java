package it.polimi.ingsw.communication.message.payload;

public class ReducedDemandCell {

    private int x;
    private int y;
    private boolean isGender;

    public ReducedDemandCell(){}

    public ReducedDemandCell(int x, int y) {
        this.x = x;
        this.y = y;
        isGender = false;
    }

    public ReducedDemandCell(ReducedWorker reducedWorker) {
        x = reducedWorker.getX();
        y = reducedWorker.getY();
        isGender = reducedWorker.isGender();
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

    public boolean isGender() {
        return isGender;
    }

    public void setGender(boolean gender) {
        isGender = gender;
    }
}
