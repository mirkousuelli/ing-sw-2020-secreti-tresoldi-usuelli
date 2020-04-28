package it.polimi.ingsw.client.view.cli;

public class NotAValidInputRunTimeException extends RuntimeException {

    public NotAValidInputRunTimeException(String errorMessage) {
        super(errorMessage);
    }
}
