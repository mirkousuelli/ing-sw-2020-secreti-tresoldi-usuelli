package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.ClientHandlerSocket;

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

    private ClientHandlerSocket connection;

    public RemoteView(String player, ClientHandlerSocket c){
        super(player);
        this.connection = c;
        c.addObserver(new MessageReceiver());
    }

    @Override
    protected void showAnswer(Answer answer) {
        System.out.println("showModel: " + answer.getPayload().toString());
        connection.asyncSend(answer);
    }
}
