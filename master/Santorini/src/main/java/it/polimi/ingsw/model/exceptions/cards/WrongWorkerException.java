package it.polimi.ingsw.model.exceptions.cards;

public class WrongWorkerException extends Exception {

    public WrongWorkerException(String errorMessage) {
        super(errorMessage);
    }
}
