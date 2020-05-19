package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.ServerClientHandler;
import it.polimi.ingsw.server.network.message.Lobby;

public class ServerStub implements ServerClientHandler {

    public Answer answer;

    public ServerStub() {
        //stub
    }

    @Override
    public void closeSocket() {
        //stub
    }

    @Override
    public void addObserver(Observer<Demand> observer) {
        //stub
    }

    @Override
    public void asyncSend(Answer message) {
        //stub
    }

    @Override
    public void send(Answer message) {
        //stub
        answer = message;
    }

    @Override
    public Lobby getLobby() {
        //stub
        return null;
    }

    @Override
    public void setLobby(Lobby lobby) {
        //stub
    }

    @Override
    public void setCreator(boolean creator) {
        //stub
    }

    @Override
    public void setActive(boolean isActive) {
        //stub
    }

    @Override
    public void setLoggingOut(boolean loggingOut) {
        //stub
    }
}
