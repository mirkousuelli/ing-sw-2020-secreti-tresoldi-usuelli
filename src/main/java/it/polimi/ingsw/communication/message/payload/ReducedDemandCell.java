package it.polimi.ingsw.communication.message.payload;

/**
 * Class that represents the reduced version of a cell that the player passes as payload in a message
 * <p>
 * For example if a player wants to make a move, he will send a message having as header a demand type of MOVE and as
 * payload a ReducedDemandCell with the coordinates of the cell he wants to move to
 * <p>
 * It contains the coordinates of this cell
 */
public class ReducedDemandCell {
    private int x;
    private int y;

    /**
     * Constructor of the reduced demand cell, setting this cell's coordinates to the one passed as parameter
     *
     * @param x x-coordinate of the cell
     * @param y y-coordinate of the cell
     */
    public ReducedDemandCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public ReducedDemandCell() {

    }

    /**
     * Constructor of the reduced demand cell, starting from the location of a worker (its reduced version). The coordinates
     * are calculated from worker's coordinates
     *
     * @param reducedWorker the worker that is on the cell which the reduced version is obtained from
     */
    public ReducedDemandCell(ReducedWorker reducedWorker) {
        x = reducedWorker.getX();
        y = reducedWorker.getY();
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
