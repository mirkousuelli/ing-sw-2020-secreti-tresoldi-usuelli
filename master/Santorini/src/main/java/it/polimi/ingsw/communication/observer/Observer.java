package it.polimi.ingsw.communication.observer;

public interface Observer<T> {

    void update(T message);
}
