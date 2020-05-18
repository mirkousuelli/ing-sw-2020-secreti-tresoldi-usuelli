package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.message.Lobby;

import java.net.Socket;

public interface ServerClientHandler {

    void closeConnection();

    void close();

    void addObserver(Observer<Demand> observer);

    void asyncSend(Answer message);

    void send(Answer message);

    Lobby getLobby();

    void setLobby(Lobby lobby);

    void setCreator(boolean creator);

    void setActive(boolean isActive);

    Socket getSocket();

    Object getBuffer();
}
