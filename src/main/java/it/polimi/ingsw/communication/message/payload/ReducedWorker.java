package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Worker;

public class ReducedWorker {

    //private final int id;
    private String owner;

    public ReducedWorker(Worker worker, String player) {
        //this.id = worker.getPlayer();
        this.owner = player;
    }

    public ReducedWorker(){}

    /*public int getId() {
        return id;
    }*/

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
