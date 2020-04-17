package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.communication.observer.Observer;


public abstract class ClientView<S> extends Observable<Demand<S>> implements Observer<Answer<S>> {

    protected final String nickName;
    private final ClientConnection<S> clientConnection;

    public ClientView(String nickName, ClientConnection<S> clientConnection) {
        this.nickName = nickName;
        this.clientConnection = clientConnection;
        clientConnection.addObserver(this);
        this.addObserver(clientConnection);
    }

    public void endGame() {
        clientConnection.closeConnection();
    }

    public abstract void startUI();
}
