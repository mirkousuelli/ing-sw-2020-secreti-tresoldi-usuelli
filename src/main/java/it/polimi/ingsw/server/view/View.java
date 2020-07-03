package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.server.observer.Observable;
import it.polimi.ingsw.server.observer.Observer;

/**
 * Abstract class that communicates with the {@code Controller} with an Observer pattern.
 * Each instance of this class represents a player
 */
public abstract class View extends Observable<ActionToPerformView> implements Observer<Answer>, IView {

    private final String player;

    /**
     * Initializes an instance of this class by setting the player's name
     *
     * @param player the player's name
     */
    protected View(String player) {
        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    /**
     * Notifies to the {@code Controller} the message received from to user
     *
     * @param demand the user's message
     */
    public void processMessage(Demand demand) {
        notify(new ActionToPerformView(player, demand, this));
    }

    /**
     * Shows the user the answer of the server to a certain action previously requested by the user itself
     *
     * @param answer the server's answer
     */
    protected abstract void showAnswer(Answer answer);

    @Override
    public void reportError(Answer answer) {
        showAnswer(answer);
    }

    @Override
    public void update(Answer answer) {
        showAnswer(answer);
    }
}
