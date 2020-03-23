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

    Block(int x, int y) {
        /* @constructor
         * it initialize the proper cell with its coordinates and set as default GROUND as level
         */
        this.x = x;
        this.y = y;
        this.pawn = null;
        this.prevLevel = Level.GROUND;
        this.currLevel = Level.GROUND;
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
    public void setX(int newX) {
        /* @setter
         * it sets the column
         */
        this.x = newX;
    }

    @Override
    public void setY(int newY) {
        /* @setter
         * it sets the row
         */
        this.y = newY;
    }

    @Override
    public void setLevel(Level newLevel) {
        /* @setter
         * it sets the level
         */
        this.currLevel = newLevel;
    }

    public void setPreviousLevel(Level oldLevel) {
        /* @setter
         * it sets the previous level
         */
        this.prevLevel = oldLevel;
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

    }

    public void addPawn(Pawn newPawn) {
        /* @function
         * adding a link to the pawn on it
         */
        if (this.isWalkable()) {
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
