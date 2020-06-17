package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;

import java.net.Socket;

/**
 * Interface which defines a common standard for a correct client-server handling approach
 */
public interface ServerClientHandler {

    /**
     * Method that close the connection
     */
    void closeSocket();

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
     * Method tha defines a synchronous sending type of answer
     *
     * @param message server answer
     */
    void send(Answer message);

    /**
     * Method that sets the player game creator
     *
     * @param creator saying if this connection is reported to the creator
     */
    void setCreator(boolean creator);

    /**
     * Method that checks if the connection belong to the creator
     *
     * @return {@code true} if connection belongs to the creator, {@code false} it doesn't belong to the creator
     */
    boolean isCreator();

    /**
     * Method that gets connection player's name
     *
     * @return {@code String} player's name
     */
    String getName();

    /**
     * Method that sets if the connection is activer or not
     *
     * @param isActive for saying connection status
     */
    void setActive(boolean isActive);

    boolean isActive();

    Socket getSocket();

    /**
     * Method that makes connection log out
     *
     * @param loggingOut for saying if logged out
     */
    void setLoggingOut(boolean loggingOut);
}
