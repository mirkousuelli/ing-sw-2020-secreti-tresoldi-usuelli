package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Worker;

/**
 * Class that represents the reduced version of a worker
 * <p>
 * It contains the nickname of its owner, its id (since every player has two workers), its coordinates and its gender
 * (this attribute is {@code true} if the worker is male, {@code false} otherwise
 */
public class ReducedWorker {

    private String owner;
    private int id;
    private int x;
    private int y;
    private boolean gender;

    /**
     * Constructor of the reduced worker, which is initialised starting from its the regular version
     *
     * @param worker the worker that the reduced version is obtained from
     * @param player the player that owns this worker
     */
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
