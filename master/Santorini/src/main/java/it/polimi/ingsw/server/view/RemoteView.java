package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.ClientConnection;

public class RemoteView extends View {

    private class MessageReceiver implements Observer<Demand> {

        @Override
        public void update(Demand demand) {
            try {
                System.out.println("Received: " + demand.toString());
                processMessage(demand);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private ClientConnection clientConnection;

    public RemoteView(String player, ClientConnection clientConnection){
        super(player);
        this.clientConnection = clientConnection;
        clientConnection.addObserver(new MessageReceiver());
    }

    @Override
    protected void showAnswer(Answer answer) {
        System.out.println("showModel: " + answer.getPayload().toString());
        clientConnection.asyncSend(answer);
    }
}
