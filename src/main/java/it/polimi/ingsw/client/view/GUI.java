package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;

public class GUI<S> extends ClientView<S>{

    ReducedPlayer player;

    public GUI(String playerName, ClientConnection clientConnection) {
        super(playerName, clientConnection);
        player = new ReducedPlayer(playerName);
    }

    @Override
    public void update(ClientModel<S> message) {
        //not implemented yet
    }

    @Override
    protected void startUI(ClientModel<S> clientModel) {
        //not implemented yet
    }
}
