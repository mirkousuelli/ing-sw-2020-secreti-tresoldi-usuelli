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
 * Class that represents a block, the concrete object described by the {@link Cell}
 * <p>
 * It contains its current and previous level, its coordinates (x and y) and the eventual pawn that can be on
 * this block
 */
public class Block implements Cell {
    private Level currLevel;
    private Level prevLevel;
    private int x;
    private int y;
    private Pawn pawn;

    /**
     * Constructor of the block, initialising the proper cell with its coordinates and sets the level to ground
     *
     * @param x the x-coordinate of the cell
     * @param y the y-coordinate of the cell
     */
    public Block(int x, int y) {
        if ((x >= 0 && x < 5) && (y >= 0 && y < 5)) {
            this.x = x;
            this.y = y;
            this.pawn = null;
            this.prevLevel = Level.GROUND;
            this.currLevel = Level.GROUND;
        }
    }

    public Block() {
    }

    @Override
    public int getX() {
        return this.x;
    }

    @Override
    public int getY() {
        return this.y;
    }

    @Override
    public Level getLevel() {
        return this.currLevel;
    }

    public Level getPreviousLevel() {
        return this.prevLevel;
    }

    public Pawn getPawn() {
        return this.pawn;
    }

    @Override
    public void setX(int newX) {
        if (newX >= 0 && newX < 5) {
            this.x = newX;
        }
    }

    @Override
    public void setY(int newY) {
        if (newY >= 0 && newY < 5) {
            this.y = newY;
        }
    }

    @Override
    public void setLevel(Level newLevel) {
        if (Arrays.asList(Level.values()).contains(newLevel)) {
            this.currLevel = newLevel;
        }
    }

    public void setPreviousLevel(Level oldLevel) {
        if (Arrays.asList(Level.values()).contains(oldLevel)) {
            this.prevLevel = oldLevel;
        }
    }

    /**
     * Method that tells if a worker can go to the block
     * <p>
     * It is always walkable unless the current level is a dome or if there is a pawn on it
     *
     * @return {@code true} if the block is walkable, {@code false} otherwise
     */
    @Override
    public boolean isWalkable() {
        return ((this.getLevel() != Level.DOME) && (this.pawn == null));
    }

    /**
     * Method that tells if the block has any pawn on it
     *
     * @return {@code true} if the block is free, {@code false} otherwise
     */
    @Override
    public boolean isFree() {
        return this.pawn == null;
    }

    /**
     * Method that checks if the block has a dome on it
     *
     * @return {@code true} if there is a dome on the block, {@code false} otherwise
     */
    @Override
    public boolean isComplete() {
        return this.getLevel() == Level.DOME;
    }

    /**
     * Method that cleans the block, removing possible pawns and setting its level back to ground
     */
    @Override
    public void clean() {
        this.currLevel = Level.GROUND;
        this.prevLevel = Level.GROUND;
        this.removePawn();
    }

    /**
     * Method that adds a pawn on the block (if it is walkable, otherwise it does nothing)
     *
     * @param newPawn the pawn to add
     */
    public void addPawn(Pawn newPawn) {
        if (this.isWalkable()) {
            // setting
            this.pawn = newPawn;
        }
    }

    /**
     * Method that removes an eventual pawn from the block
     */
    public void removePawn() {
        this.pawn = null;
    }
}
