package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.observer.Observer;

/**
 * Interface which defines a common standard for a correct client-server handling approach
 */
public interface ServerClientHandler {

    /**
     * Method that adds a new observer oriented to a Demand object
     *
     * @param observer new demand observer
     */
    void addObserver(Observer<Demand> observer);

    /**
     * Method that defines an asynchronous sending type of answer
     *
     * @param message server answer
     */
    void asyncSend(Answer message);

    /**
     * Method that defines a synchronous sending type of answer
     *
     * @param message server answer
     */
    void send(Answer message);

    /**
     * Method that checks if the connection belong to the creator
     *
     * @return {@code true} if connection belongs to the creator, {@code false} it doesn't belong to the creator
     */
    boolean isCreator();

    String getName();
}
