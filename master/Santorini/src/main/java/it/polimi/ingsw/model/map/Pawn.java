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
import it.polimi.ingsw.model.exceptions.map.*;

import java.util.Arrays;

public abstract class Pawn implements Cell {
    /* @abstractClass
     * it represents a general pawn on the grill in order to give general indication for whatever object
     * different from a tower block can behave inside the board.
     */

    protected Block currCell; // current position in the grill
    protected Player player; // pawn owner who can manage it

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */

    public Pawn (Player player, Block currCell) throws NullPointerException, PawnPositioningException {
        /* @constructor
         * it constructs a pawn linking the owner (player) and is current position within the board
         */

        if (player == null || currCell == null) {
            throw new NullPointerException();
        }

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

    public Cell getLocation() {
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
    public void setX(int newX) throws NotValidCellException {
        /* @setter
         * it sets the column
         */

        if (newX < 0 || newX >= 5) {
            throw new NotValidCellException("Not valid X value!");
        }

        this.currCell.setX(newX);
    }

    @Override
    public void setY(int newY) throws NotValidCellException {
        /* @setter
         * it sets the row
         */

        if (newY < 0 || newY >= 5) {
            throw new NotValidCellException("Not valid X value!");
        }

        this.currCell.setY(newY);
    }

    public void setLocation(Block newCell) throws NullPointerException, PawnPositioningException {
        /* @setter
         * it sets the current position
         */

        if (newCell == null) {
            throw new NullPointerException();
        }

        this.currCell.removePawn();
        this.currCell = newCell;
        this.currCell.addPawn(this);
    }

    public void setPlayer(Player newPlayer) throws NullPointerException {
        /* @setter
         * it sets the pawn owner
         */

        if (newPlayer == null) {
            throw new NullPointerException();
        }

        this.player = newPlayer;
    }

    @Override
    public void setLevel(Level newLevel) throws NullPointerException, NotValidLevelException {
        /* @setter
         * it sets the level
         */

        if (newLevel == null) {
            throw new NullPointerException();
        }

        if (!Arrays.asList(Level.values()).contains(newLevel)) {
            throw new NotValidLevelException("Invalid level inserted!");
        }

        this.currCell.setLevel(newLevel);
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
