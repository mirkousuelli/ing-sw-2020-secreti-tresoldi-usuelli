/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.map;

import it.polimi.ingsw.model.Player;

import java.util.List;

public class Worker extends Pawn {
    /* @class
     * it represents the pawn 'worker' which can be moved to one cell distance from its current location (default),
     * and then build around one block up to the selected cell (except for its new position).
     */

    private Block prevCell;
    private Block prevBuild;

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */

    public Worker(Player player, Block pos) {
        /* @constructor
         * it re-calls its super class Pawn
         */
        super(player, pos);
        this.prevCell = pos;
        this.prevBuild = null;
    }

    /* GETTER  --------------------------------------------------------------------------------------------------------- */

    public Cell getPreviousLocation() {
        /* @getter
         * it gets previous worker's location
         */
        return prevCell;
    }

    public Cell getPreviousBuild() {
        /* @getter
         * it gets the previous building built
         */
        return prevBuild;
    }

    public List<Cell> getSpecialMoves() {
        /* @function
         * returns all special moves that can be activated by some gods where
         * around cell are busy from other players' workers
         */

        List<Cell> toReturn = this.currCell.getAround();

        for (Cell around : this.currCell.getAround()) {
            // if it is busy or complete or higher than allowed
            if (around.isFree()) {
                // then remove it from the list
                toReturn.remove(around);
            }
        }
    }

    public List<Cell> getPossibleMoves() {
        /* @getter
         * it considers malus attributes in player and modify possible around cells
         */
        List<Cell> toReturn = this.currCell.getAround();

        // checking for around cell higher than allowed
        for (Cell around : this.currCell.getAround()) {
            // if it is busy or complete or higher than allowed
            if (!around.isWalkable() || this.currCell.getLevel().toInt() + 1 < around.getLevel().toInt()) {
                // then remove it from the list
                toReturn.remove(around);
            }
        }

        // cannot move up malus active
        if (this.player.isCannotMoveUpActive()) {
            // look for everything around
            for (Cell around : this.currCell.getAround()) {
                // checking level difference
                if (this.currCell.getLevel().toInt() < around.getLevel().toInt()) {
                    //removing from the list to return
                    toReturn.remove(around);
                }
            }
            // if everything is removed then i will return null
        }

        // must move up malus active
        if (this.player.isMustMoveUpActive()) {
            // look for everything around
            for (Cell around : this.currCell.getAround()) {
                // checking level difference
                if (this.currCell.getLevel().toInt() > around.getLevel().toInt()) {
                    //removing from the list to return
                    toReturn.remove(around);
                }
            }

            // in case i removed everything i reset around
            if (toReturn == null) {
                toReturn = this.currCell.getAround();
            }
        }

        // in case no malus has been active : normal getAround()
        // in case both malus are active : normal getAround()
        return toReturn;
    }

    public List<Cell> getPossibleBuilds() {
        /* @getter
         * it gets possible cell where to build
         */

        // in this case, since no gods have consequence on it, it doesn't change
        return this.currCell.getAround();
    }

    /* SETTER ---------------------------------------------------------------------------------------------------------- */

    public void setPreviousLocation(Cell prevCell) {
        /* @setter
         * it sets previous worker's location
         */

        /*if (prevCell == null) {
            throw new NullPointerException();
        }

        if ((prevCell.getX() >= 5 || prevCell.getX() < 0) && (prevCell.getY() >= 5 || prevCell.getY() < 0)) {
            throw new NotValidCellException("Coordinates out of domain!");
        }*/

        this.prevCell = (Block) prevCell;
    }

    public void setPreviousBuild(Cell prevBuild) {
        /* @setter
         * it sets the previous block built
         */

        /*if (prevCell == null) {
            throw new NullPointerException();
        }

        if ((prevBuild.getX() >= 5 || prevBuild.getX() < 0) && (prevBuild.getY() >= 5 || prevBuild.getY() < 0)) {
            throw new NotValidCellException("Coordinates out of domain!");
        }*/

        this.prevBuild = (Block) prevBuild;
    }

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */

    public boolean moveTo(Cell newCell) {
        /* @function
         * it makes worker moving to another cell going through an operation of undecorate-decorate
         */

        /*if (newCell == null) {
            throw new NullPointerException();
        }*/

        if (newCell.equals(this.currCell)) {
            // self moving
            return true;
        }

        // if it is not a dome, free and it is contained within possible choices
        if (newCell.isWalkable() && this.getPossibleMoves().contains(newCell)) {
            // removing pawn from the current cell
            this.currCell.removePawn();

            // updating previous cell with the old current cell
            this.prevCell = this.currCell;

            // updating current cell with the new cell just moved on
            this.currCell = (Block) newCell;

            // adding new pawn on the current new cell
            this.currCell.addPawn(this);

            // returning everything correct
            return true;

        }

        /*if (!newCell.isFree()){
            throw new OccupiedCellException("Selected cell is busy!");
        }

        if (newCell.isComplete()) {
            throw new OutOfAroundException("Selected cell is complete!");
        }

        if (!this.getPossibleMoves().contains(newCell)) {
            throw new OutOfAroundException("You selected a cell not around your worker!");
        }*/

        // try again
        return false;
    }

    public boolean build(Cell cellToBuildUp) {
        /* @function
         * it builds around except for its current location (by default), unless a god change this rule
         */
        /*if (cellToBuildUp == null) {
            throw new NullPointerException();
        }*/

        Block toBuild = (Block) cellToBuildUp;

        // if it is not a dome, free and it is contained within possible choices
        if (toBuild.isWalkable() && this.getPossibleBuilds().contains(toBuild)) {
            // storing previous level
            toBuild.setPreviousLevel(toBuild.getLevel());

            // then build it up
            toBuild.setLevel(toBuild.getLevel().buildUp());

            // updating last building
            this.setPreviousBuild(toBuild);

            // returning everything correct
            return true;
        }

        /*if (!toBuild.isFree()){
            throw new OccupiedCellException("Selected cell is busy!");
        }

        if (toBuild.isComplete()) {
            throw new OutOfAroundException("Selected cell is complete!");
        }

        if (!this.getPossibleBuilds().contains(toBuild)) {
            throw new OutOfAroundException("You selected a cell not around your worker!");
        }*/

        // try again
        return false;
    }

    /* PAWN_ABSTRACT_METHODS ------------------------------------------------------------------------------------------- */

    @Override
    public boolean isMovable() {
        /* @predicate
         * it indicates that the pawn worker is able to change is position
         */
        return true;
    }
}
