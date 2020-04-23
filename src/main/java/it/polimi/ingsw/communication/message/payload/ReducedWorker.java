package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Worker;

public class ReducedWorker {

    //private final int id;
    private String owner;
    private int x;
    private int y;

    public ReducedWorker(Worker worker, String player) {
        //this.id = worker.getPlayer();
        this.owner = player;
        this.x = worker.getX();
        this.y = worker.getY();
    }

    public ReducedWorker() {}

    /*public int getId() {
        return id;
    }*/

    public String getOwner() {
        return owner;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
