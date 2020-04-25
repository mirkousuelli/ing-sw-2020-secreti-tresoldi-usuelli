package it.polimi.ingsw.client.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;

import java.io.IOException;

public interface ClientConnection<S> extends Observer<Demand<S>> {
    //RMI or Socket

    //Observer
    void update(Demand<S> message);

    //Observalbe
    void addObserver(Observer<Answer<S>> observer);

    void removeObserver(Observer<Answer<S>> observer);

    void notify(Answer<S> answer);

    //Thread
    void startClient() throws IOException;

    void closeConnection() throws IOException;
}
