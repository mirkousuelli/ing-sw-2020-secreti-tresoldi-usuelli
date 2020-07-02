package it.polimi.ingsw.server.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the objects that are observed
 */
public class Observable<T> {

    private final List<Observer<T>> observers = new ArrayList<>();

    /**
     * Method that adds a new observer to the list of observers
     *
     * @param observer the observer that is added
     */
    public void addObserver(Observer<T> observer) {
        synchronized (observers) {
            observers.add(observer);
        }
    }

    /**
     * Method that removes the selected observer from the list of observers
     *
     * @param observer the observer that is removed
     */
    public void removeObserver(Observer<T> observer) {
        synchronized (observers) {
            observers.remove(observer);
        }
    }

    /**
     * Method that allows this observable to notify the observers with the message
     *
     * @param message the object that the observers are notified of
     */
    public void notify(T message) {
        synchronized (observers) {
            for (Observer<T> observer : observers) {
                observer.update(message);
            }
        }
    }
}
