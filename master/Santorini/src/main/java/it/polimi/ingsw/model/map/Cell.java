package it.polimi.ingsw.model.map;

public interface Cell {
    /*
     *
     */

    Level level = Level.GROUND;
    int row = 0;
    int col = 0;

    public boolean isWalkable();
}
