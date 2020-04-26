package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.communication.observer.Observer;

import java.io.IOException;

public abstract class ClientView<S> extends Observable<Demand<S>> implements Observer<ClientModel<S>> {

    protected final ReducedPlayer player;
    private final ClientConnection<S> clientConnection;
    protected boolean notified;

    public ClientView(ReducedPlayer player, ClientConnection<S> clientConnection) {
        this.player = player;
        this.clientConnection = clientConnection;
        notified = false;
    }

    public ClientView(String playerName, ClientConnection<S> clientConnection) {
        player = new ReducedPlayer(playerName);
        this.clientConnection = clientConnection;
    }

    public ReducedPlayer getPlayer() {
        return player;
    }

    public ClientConnection<S> getClientConnection() {
        return clientConnection;
    }

    public void setNotified() {
        this.notified = true;
    }

    protected void endGame() throws IOException {
        clientConnection.closeConnection();
    }

    public abstract void run(ClientModel<S> clientModel);
}
