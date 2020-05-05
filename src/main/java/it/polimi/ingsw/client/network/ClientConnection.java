package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;

import java.io.IOException;

public interface ClientConnection<S> {
    //RMI or Socket

    //Thread
    void run();

    void closeConnection() throws IOException;

    boolean isChanged();

    boolean hasAnswer();

    void setChanged(boolean isChanged);

    Answer<S> getAnswer();

    void setClientView(ClientView<S> clientView);
}
