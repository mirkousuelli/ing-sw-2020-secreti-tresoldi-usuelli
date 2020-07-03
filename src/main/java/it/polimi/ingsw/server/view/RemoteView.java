package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.network.ServerClientHandler;
import it.polimi.ingsw.server.observer.Observer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that acts as a middleman between {@code ServerClientHandler} and {@code Controller}
 * <p>
 * It receives a message from one of them and notifies it to the other one. It communicates with them with an Observer pattern
 */
public class RemoteView extends View {

    /**
     * Private class that communicates with {@code ServerClientHandler} with an Observer pattern
     */
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

    /**
     * Initializes a remove view's instance by setting the player's name and handler
     *
     * @param player              the player's name
     * @param serverClientHandler the player's socket handler
     */
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
