package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.message.Lobby;

public interface ServerClientHandler {

    void closeConnection();

    void addObserver(Observer<Demand> observer);

    void asyncSend(Answer message);

    void send(Answer message);

    Lobby getLobby();

    void setLobby(Lobby lobby);
}
