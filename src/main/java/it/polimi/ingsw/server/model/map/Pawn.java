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

/**
 * Abstract class that represents a generic pawn, giving indication for how any object (different from a block) can
 * behave on the board
 * <p>
 * It contains the current cell on the board
 * <p>
 * In our game it can only be a worker but some advanced God power may require the use of tokens, therefore making
 * it easier to add them into the game
 * <p>
 * It extends {@link Cell}
 */
public abstract class Pawn implements Cell {

    protected Block currCell;

    /**
     * Constructor of the pawn, adding it on the chosen block
     *
     * @param currCell the block where the pawn is added
     */
    public Pawn (Block currCell) {
        this.currCell = currCell;
        this.currCell.addPawn(this);
    }

    @Override
    public int getX() {
        return this.currCell.getX();
    }

    @Override
    public int getY() {
        return this.currCell.getY();
    }

    public Block getLocation() {
        return this.currCell;
    }

    @Override
    public Level getLevel() {
       return this.currCell.getLevel();
    }

    @Override
    public void setX(int newX) {
        if (newX >= 0 && newX < 5) {
            this.currCell.setX(newX);
        }
    }

    @Override
    public void setY(int newY) {
        if (newY >= 0 && newY < 5) {
            this.currCell.setY(newY);
        }
    }

    public void setLocation(Block newCell) {
        this.currCell.removePawn();
        this.currCell = newCell;
        this.currCell.addPawn(this);
    }

    @Override
    public void setLevel(Level newLevel) {
        if (Arrays.asList(Level.values()).contains(newLevel)) {
            this.currCell.setLevel(newLevel);
        }
    }


    /* @abstractMethod - @predicate
     * it says if the pawn can change its location and it is not defined in here since it depends from the kind
     * of pawn (for instance token cannot move whereas workers can)
     */
    public abstract boolean isMovable();


    /**
     * Method that checks if the current cell is walkable
     *
     * @return {@code true} if the cell is walkable, {@code false} otherwise
     */
    @Override
    public boolean isWalkable() {
        return this.currCell.isWalkable();
    }

    /**
     * Method that checks if the cell has a dome on it
     *
     * @return {@code true} if there is a dome on the cell, {@code false} otherwise
     */
    @Override
    public boolean isComplete() {
        return this.currCell.isComplete();
    }

    /**
     * Method that checks if the current cell is free
     *
     * @return {@code true} if the cell is free, {@code false} otherwise
     */
    @Override
    public boolean isFree() {
        return this.currCell.isFree();
    }

    /**
     * Method that removes an eventual pawn from the cell
     */
    @Override
    public void clean() {
        this.currCell.clean();
        this.currCell = null;
    }
}
