package it.polimi.ingsw.client.view;

import it.polimi.ingsw.communication.message.payload.ReducedPlayer;

public class GUI<S> extends ClientView<S>{

    ReducedPlayer player;

    public GUI(String playerName, ClientModel clientModel) {
        super(playerName, clientModel);
        player = new ReducedPlayer(playerName);
    }

    @Override
    public void run() {
        //not implemented yet
    }
}
