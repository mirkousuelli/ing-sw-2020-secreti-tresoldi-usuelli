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

public class Worker extends Pawn {
    /* @class
     * it represents the pawn 'worker' which can be moved to one cell distance from its current location (default),
     * and then build around one block up to the selected cell (except for its new position).
     */

    private Cell prevCell;
    private Cell prevBuild;

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */

    Worker(Player player, Block pos) {
        /* @constructor
         * it re-call its super class Pawn
         */
        super(player, pos);
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

    /* SETTER ---------------------------------------------------------------------------------------------------------- */

    public void setPreviousLocation(Cell prevCell) {
        /* @setter
         * it sets previous worker's location
         */
        this.prevCell = prevCell;
    }

    public void setPreviousBuild(Cell prevBuild) {
        /* @setter
         * it sets the previous block built
         */
        this.prevBuild = prevBuild;
    }

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */

    public boolean moveTo(Cell newCell) {
        /* @function
         * it makes worker moving to another cell going through an operation of undecorate-decorate
         */
        return true;
    }

    public boolean buildUp(Cell cellToBuildUp) {
        /* @function
         * it builds around except for its current location (by default), unless a god change this rule
         */
        currCell.setLevel(currCell.getLevel().buildUp());
        return true;
    }

    public boolean buildDown(Cell cellToBuildDown) {
        /* @function
         * it decrease current cell level selected
         */
        currCell.setLevel(currCell.getLevel().buildDown());
        return true;
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
