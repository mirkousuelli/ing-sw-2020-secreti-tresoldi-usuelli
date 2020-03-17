package it.polimi.ingsw.model.map;

public class Worker implements Pawn {
    /*
     *
     */
    private Cell previousCell;

    Worker() {

    }

    public Cell getLocation() {
        /*
         *
         */
        return origin;
    }

    public void setLocation() {
        /*
         *
         */
        ;
    }

    public boolean build() {
        /*
         *
         */
        return true;
    }

    public boolean moveTo() {
        /*
         *
         */
        return true;
    }

    public boolean isMoovable() {
        /*
         *
         */
        return true;
    }

    public Cell getPreviousLocation() {
        /*
         *
         */
        return null;
    }

    @Override
    public boolean isWalkable() {
        /*
         *
         */
        return false;
    }
}
