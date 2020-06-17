package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.ServerClientHandler;

import java.net.Socket;

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
    public void setCreator(boolean creator) {
        //stub
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

    @Override
    public void setActive(boolean isActive) {
        //stub
    }

    @Override
    public boolean isActive() {
        //stub
        return false;
    }

    @Override
    public Socket getSocket() {
        //stub
        return null;
    }

    @Override
    public void setLoggingOut(boolean loggingOut) {
        //stub
    }
}
