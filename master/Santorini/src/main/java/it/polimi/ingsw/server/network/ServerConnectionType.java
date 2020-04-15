package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;

public interface ServerConnectionType {

    void closeConnection();

    void addObserver(Observer<Demand> observer);

    void asyncSend(Object message);
}
