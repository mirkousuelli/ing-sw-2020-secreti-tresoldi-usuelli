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

    public void setNotified() {
        this.notified = true;
    }

    protected abstract void startUI(ClientModel<S> clientModel);

    protected void endGame() throws IOException {
        clientConnection.closeConnection();
    }

    public void run(ClientModel<S> clientModel) {
        while (clientConnection.isActive()) {
            if (notified) {
                startUI(clientModel);
                notified = false;
            }
        }
    }
}
