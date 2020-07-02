package it.polimi.ingsw.server.observer;

/**
 * Interface that represents the object that are observers
 */
public interface Observer<T> {

    /**
     * Method that allows the observers to be updated with the message.
     * <p>
     * It is developed deeper from the classes that implements this one
     *
     * @param message the object that the observers are notified of
     */
    void update(T message);
}
