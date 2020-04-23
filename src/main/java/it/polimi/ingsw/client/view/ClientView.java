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

    public ClientView(ReducedPlayer player, ClientConnection<S> clientConnection) {
        this.player = player;
        this.clientConnection = clientConnection;
    }

    public ReducedPlayer getPlayer() {
        return player;
    }

    protected void endGame() throws IOException {
        clientConnection.closeConnection();
    }
}
