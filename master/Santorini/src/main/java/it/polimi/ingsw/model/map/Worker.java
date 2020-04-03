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

    public Block getPreviousLocation() {
        /* @getter
         * it gets previous worker's location
         */

        return prevCell;
    }

    public Block getPreviousBuild() {
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

        if ((prevCell.getX() < 5 && prevCell.getX() >= 0) && (prevCell.getY() < 5 && prevCell.getY() >= 0)) {
            this.prevCell = (Block) prevCell;
        }
    }

    public void setPreviousBuild(Cell prevBuild) {
        /* @setter
         * it sets the previous block built
         */

        if ((prevBuild.getX() < 5 && prevBuild.getX() >= 0) && (prevBuild.getY() < 5 && prevBuild.getY() >= 0)) {
            this.prevBuild = (Block) prevBuild;;
        }
    }

    @Override
    public boolean isMovable() {
        /* @predicate
         * it indicates that the pawn worker is able to change is position
         */
        return true;
    }
}
