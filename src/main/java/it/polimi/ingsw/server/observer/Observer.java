package it.polimi.ingsw.server.observer;

public interface Observer<T> {

    void update(T message);
}
