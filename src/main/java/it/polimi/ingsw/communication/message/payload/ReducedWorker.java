package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Worker;

public class ReducedWorker {

    private String owner;
    private int x;
    private int y;

    public ReducedWorker(Worker worker, String player) {
        this.owner = player;
        this.x = worker.getX();
        this.y = worker.getY();
    }

    public ReducedWorker() {}

    public String getOwner() {
        return owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

}
