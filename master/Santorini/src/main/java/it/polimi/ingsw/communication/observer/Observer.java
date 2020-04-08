package it.polimi.ingsw.communication.observer;

public interface Observer<T> {

    public void update(T message);

}
