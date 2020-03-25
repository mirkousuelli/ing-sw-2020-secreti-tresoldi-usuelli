package it.polimi.ingsw.model.exceptions.map;

public class OccupiedCellException extends Exception {

    public OccupiedCellException(String errorMessage) {
        super(errorMessage);
    }
}
