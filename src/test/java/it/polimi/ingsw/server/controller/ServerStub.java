package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.observer.Observer;
import it.polimi.ingsw.server.network.ServerClientHandler;

public class ServerStub implements ServerClientHandler {

    Answer answer;

    ServerStub() {
        //stub
    }

    @Override
    public void addObserver(Observer<Demand> observer) {
        //stub
    }

    @Override
    public void asyncSend(Answer message) {
        //stub
        answer = message;
    }

    @Override
    public void send(Answer message) {
        //stub
        answer = message;
    }

    @Override
    public boolean isCreator() {
        //stub
        return false;
    }

    @Override
    public String getName() {
        //stub
        return null;
    }
}
