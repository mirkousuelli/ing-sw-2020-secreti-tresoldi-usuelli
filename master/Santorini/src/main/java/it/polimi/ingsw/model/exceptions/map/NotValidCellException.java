package it.polimi.ingsw.model.exceptions.map;

public class NotValidCellException extends Exception {
    public NotValidCellException(String errorMessage) {
        super(errorMessage);
    }
}
