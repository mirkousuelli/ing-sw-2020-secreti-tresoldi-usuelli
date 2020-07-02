package it.polimi.ingsw.server.view;

import it.polimi.ingsw.communication.message.Answer;

/**
 * Interface used to show to the {@code Controller} only the methods to perform a callback to the remove view
 */
public interface IView {

    /**
     * Performs a callback to the remove view
     *
     * @param answer the error message to return to the remove view
     */
    void reportError(Answer answer);
}
