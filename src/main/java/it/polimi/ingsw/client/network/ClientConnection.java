package it.polimi.ingsw.client.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;

import java.io.IOException;

public interface ClientConnection<S> extends Observer<Demand<S>> {
    //RSI or Socket
    void update(Demand<S> message);


    void addObserver(Observer<Answer<S>> observer);

    void removeObserver(Observer<Answer<S>> observer);

    void notify(Answer<S> answer);


    void closeConnection() throws IOException;
}
