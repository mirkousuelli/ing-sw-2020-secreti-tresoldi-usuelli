package it.polimi.ingsw.client.view.cli;

public class NotAValidTurnRunTimeException extends RuntimeException {

    public NotAValidTurnRunTimeException(String errorMessage) {
        super(errorMessage);
    }
}
