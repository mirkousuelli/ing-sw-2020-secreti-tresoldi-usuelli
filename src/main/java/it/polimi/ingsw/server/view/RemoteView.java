package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.network.ServerClientHandler;
import it.polimi.ingsw.server.observer.Observer;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteView extends View {

    private class MessageReceiver implements Observer<Demand> {

        private final Logger logger = Logger.getLogger(MessageReceiver.class.getName());

        @Override
        public void update(Demand demand) {
            try {
                processMessage(demand);
            } catch (Exception e) {
                logger.log(Level.SEVERE, e.toString(), e);
            }
        }
    }

    private final ServerClientHandler serverClientHandler;

    public RemoteView(String player, ServerClientHandler serverClientHandler) {
        super(player);
        this.serverClientHandler = serverClientHandler;
        serverClientHandler.addObserver(new MessageReceiver());
    }

    @Override
    protected void showAnswer(Answer answer) {
        serverClientHandler.send(answer);
    }
}
