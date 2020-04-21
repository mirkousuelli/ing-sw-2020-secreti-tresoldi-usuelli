package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.observer.Observer;
import it.polimi.ingsw.server.network.ServerClientHandler;

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

    private ServerClientHandler serverClientHandler;

    public RemoteView(String player, ServerClientHandler serverClientHandler){
        super(player);
        this.serverClientHandler = serverClientHandler;
        serverClientHandler.addObserver(new MessageReceiver());
    }

    @Override
    protected void showAnswer(Answer answer) {
        System.out.println("showModel: " + answer.getPayload().toString());
        serverClientHandler.asyncSend(answer);
    }
}
