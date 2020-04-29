package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.message.Lobby;

public interface ServerClientHandler {

    void closeConnection();

    void addObserver(Observer<Demand> observer);

    void asyncSend(Message message);

    Lobby getLobby();

    void setLobby(Lobby lobby);
}
