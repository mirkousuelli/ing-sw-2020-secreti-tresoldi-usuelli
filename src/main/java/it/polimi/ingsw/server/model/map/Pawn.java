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

import it.polimi.ingsw.server.model.Player;

import java.util.Arrays;

public abstract class Pawn implements Cell {
    /* @abstractClass
     * it represents a general pawn on the grill in order to give general indication for whatever object
     * different from a tower block can behave inside the board.
     */

    protected Block currCell; // current position in the grill
    protected Player player; // pawn owner who can manage it

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */

    public Pawn (Player player, Block currCell) {
        /* @constructor
         * it constructs a pawn linking the owner (player) and is current position within the board
         */

        this.player = player;
        this.currCell = currCell;
        this.currCell.addPawn(this);
    }

    /* GETTER ---------------------------------------------------------------------------------------------------------- */

    @Override
    public int getX() {
        /* @getter
         * it gets column
         */

        return this.currCell.getX();
    }

    @Override
    public int getY() {
        /* @getter
         * it gets row
         */

        return this.currCell.getY();
    }

    public Block getLocation() {
        /* @getter
         * it gets the current position
         */

        return this.currCell;
    }

    public Player getPlayer() {
        /* @getter
         * it gets the pawn owner
         */

        return this.player;
    }

    @Override
    public Level getLevel() {
        /* @getter
         * it gets the level
         */

        return this.currCell.getLevel();
    }

    /* SETTER ---------------------------------------------------------------------------------------------------------- */

    @Override
    public void setX(int newX) {
        /* @setter
         * it sets the column
         */

        if (newX >= 0 && newX < 5) {
            this.currCell.setX(newX);
        }
    }

    @Override
    public void setY(int newY) {
        /* @setter
         * it sets the row
         */

        if (newY >= 0 && newY < 5) {
            this.currCell.setY(newY);
        }
    }

    public void setLocation(Block newCell) {
        /* @setter
         * it sets the current position
         */

        this.currCell.removePawn();
        this.currCell = newCell;
        this.currCell.addPawn(this);
    }

    public void setPlayer(Player newPlayer) {
        /* @setter
         * it sets the pawn owner
         */

        this.player = newPlayer;
    }

    @Override
    public void setLevel(Level newLevel) {
        /* @setter
         * it sets the level
         */

        if (Arrays.asList(Level.values()).contains(newLevel)) {
            this.currCell.setLevel(newLevel);
        }
    }

    /* ABSTRACT_METHOD ------------------------------------------------------------------------------------------------- */

    /* @abstractMethod - @predicate
     * it says if the pawn can change its location and it is not defined in here since it depends from the kind
     * of pawn (for instance token cannot move whereas workers yes instead)
     */
    abstract public boolean isMovable();

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */

    @Override
    public boolean isWalkable() {
        /* @predicate
         * it imposes that nothing can walk on a pawn
         */

        return this.currCell.isWalkable();
    }

    @Override
    public boolean isComplete() {
        /* @predicate
         * it asks if the current cell is complete
         */

        return this.currCell.isComplete();
    }

    @Override
    public boolean isFree() {
        /* @predicate
         * it asks if the current cell is complete
         */

        return this.currCell.isFree();
    }

    @Override
    public void clean() {
        /* @function
         * it cleans off the current cell to its starting state
         */

        this.currCell.clean();
        this.currCell = null;
    }
}
