package it.polimi.ingsw.model.cards.gods.exceptions;

public class OccupiedCellException extends Exception {

    public OccupiedCellException(String errorMessage) {
        super(errorMessage);
    }
}
