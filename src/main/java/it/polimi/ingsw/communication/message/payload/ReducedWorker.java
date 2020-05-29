package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Worker;

public class ReducedWorker {

    private String owner;
    private int id;
    private int x;
    private int y;
    private boolean gender;

    public ReducedWorker(Worker worker, String player) {
        this.owner = player;
        id = worker.getId();
        x = worker.getX();
        y = worker.getY();
        gender = worker.isMale();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
