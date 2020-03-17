package it.polimi.ingsw.model.map;

import java.util.ArrayList;
import java.util.List;

public class Board {
    /*
     *
     *
     */
    private final int NUM_CELL = 25;
    private List<Cell> map;

    Board() {
        /*
         *
         */
        map = new ArrayList<Cell>(NUM_CELL);
    }

    public List<Cell> getSurround(Cell cell) {
        /*
         *
         *
         */
        return null;
    }
}
