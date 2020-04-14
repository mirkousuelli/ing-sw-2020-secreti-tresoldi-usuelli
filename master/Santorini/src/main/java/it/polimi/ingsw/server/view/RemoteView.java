package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.ServerConnectionType;

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

    private ServerConnectionType serverConnectionType;

    public RemoteView(String player, ServerConnectionType serverConnectionType){
        super(player);
        this.serverConnectionType = serverConnectionType;
        serverConnectionType.addObserver(new MessageReceiver());
    }

    @Override
    protected void showAnswer(Answer answer) {
        System.out.println("showModel: " + answer.getPayload().toString());
        serverConnectionType.asyncSend(answer);
    }
}
