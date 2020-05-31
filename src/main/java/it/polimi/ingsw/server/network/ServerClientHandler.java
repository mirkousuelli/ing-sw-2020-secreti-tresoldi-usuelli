package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;

public interface ServerClientHandler {

    void closeSocket();

    void addObserver(Observer<Demand> observer);

    void asyncSend(Answer message);

    void send(Answer message);

    void setCreator(boolean creator);

    boolean isCreator();

    String getName();

    void setActive(boolean isActive);

    void setLoggingOut(boolean loggingOut);
}
