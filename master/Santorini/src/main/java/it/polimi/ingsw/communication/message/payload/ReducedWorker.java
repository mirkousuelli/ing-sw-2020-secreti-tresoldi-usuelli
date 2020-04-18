package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Worker;

public class ReducedWorker {

    //private final int id;
    private final String owner;

    public ReducedWorker(Worker worker, String player) {
        //this.id = worker.id;
        this.owner = player;
    }

    /*public int getId() {
        return id;
    }*/

    public String getOwner() {
        return owner;
    }
}
