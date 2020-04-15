package it.polimi.ingsw.client.view;

import it.polimi.ingsw.client.model.ClientModel;
import it.polimi.ingsw.client.network.ClientConnectionType;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observable;
import it.polimi.ingsw.communication.observer.Observer;

public abstract class ClientView extends Observable<Demand> implements Observer<ClientModel> {

    protected ClientModel clientModel;

    public ClientView(ClientConnectionType clientConnection, ClientModel clientModel) {
        this.clientModel = clientModel;
        clientModel.addObserver(this);
        //this.addObserver(clientConnection);
    }

    public abstract void startUI();
}
