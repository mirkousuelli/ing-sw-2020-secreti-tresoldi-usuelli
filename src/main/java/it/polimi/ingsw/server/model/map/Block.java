/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.map;

import java.util.Arrays;

public class Block implements Cell {
    /* @class
     * it represents the concrete object described by the cell
     */

    private Level currLevel;
    private Level prevLevel;
    private int x;
    private int y;
    private Pawn pawn;

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */

    public Block(int x, int y) {
        /* @constructor
         * it initialize the proper cell with its coordinates and set as default GROUND as level
         */

        if ((x >= 0 && x < 5) && (y >= 0 && y < 5)) {
            this.x = x;
            this.y = y;
            this.pawn = null;
            this.prevLevel = Level.GROUND;
            this.currLevel = Level.GROUND;
        }
    }

    /* GETTER ---------------------------------------------------------------------------------------------------------- */

    @Override
    public int getX() {
        /* @getter
         * it gets which column
         */

        return this.x;
    }

    @Override
    public int getY() {
        /* @getter
         * it gets which row
         */

        return this.y;
    }

    @Override
    public Level getLevel() {
        /* @getter
         * it gets the current level
         */

        return this.currLevel;
    }

    public Level getPreviousLevel() {
        /* @getter
         * it gets the previous level state of the cell (useful for Atlas)
         */

        return this.prevLevel;
    }

    public Pawn getPawn() {
        /* @getter
         * it gives back the eventual pawn on it
         */

        return this.pawn;
    }

    /* SETTER ---------------------------------------------------------------------------------------------------------- */
    @Override
    public void setX(int newX){
        /* @setter
         * it sets the column
         */

        if (newX >= 0 && newX < 5) {
            this.x = newX;
        }
    }

    @Override
    public void setY(int newY) {
        /* @setter
         * it sets the row
         */

        if (newY >= 0 && newY < 5) {
            this.y = newY;
        }
    }

    @Override
    public void setLevel(Level newLevel) {
        /* @setter
         * it sets the level
         */

        if (Arrays.asList(Level.values()).contains(newLevel)) {
            this.currLevel = newLevel;
        }
    }

    public void setPreviousLevel(Level oldLevel) {
        /* @setter
         * it sets the previous level
         */

        if (Arrays.asList(Level.values()).contains(oldLevel)) {
            this.prevLevel = oldLevel;
        }
    }

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean isWalkable() {
        /* @predicate
         * it is always walkable unless the current level is a dome.
         */

        return ((this.getLevel() != Level.DOME) && (this.pawn == null));
    }

    @Override
    public boolean isFree() {
        /* @predicate
         * ask if it is free
         */

        return this.pawn == null;
    }

    @Override
    public boolean isComplete() {
        /* @predicate
         * ask if it is complete
         */

        return this.getLevel() == Level.DOME;
    }

    @Override
    public void clean() {
        /* @function
         * clean off the cell to its starting state
         */

        this.currLevel = Level.GROUND;
        this.prevLevel = Level.GROUND;
        this.removePawn();
    }

    public void addPawn(Pawn newPawn) {
        /* @function
         * adding a link to the pawn on it
         */

        if (this.isWalkable()) {
            // setting
            this.pawn = newPawn;
        }
    }

    public void removePawn() {
        /* @function
         * remove the pawn from it
         */

        this.pawn = null;
    }
}
