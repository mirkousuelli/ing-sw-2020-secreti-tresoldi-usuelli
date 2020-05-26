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

/**
 * Class that represents the pawn "worker"
 * <p>
 * It contains its previous cell, previous build, gender and id
 */
public class Worker extends Pawn {
    private Block prevCell;
    private Block prevBuild;
    private boolean gender;
    private int id;

    /**
     * Constructor of the worker, re-calling its super class Pawn
     *
     * @param pos the block where the worker is placed
     */
    public Worker(Block pos) {
        super(pos);
        this.prevCell = pos;
        this.prevBuild = null;
        this.gender = true;
    }

    /**
     * Constructor of the worker, re-calling its super class Pawn
     *
     * @param id the number that identifies the worker
     * @param pos the block where the worker is placed
     */
    public Worker(int id, Block pos) {
        super(pos);
        this.prevCell = pos;
        this.prevBuild = null;
        this.gender = true;
        this.id = id;
    }

    public Block getPreviousLocation() {
        return prevCell;
    }

    public Block getPreviousBuild() {
        return prevBuild;
    }

    public int getId() {
        return id;
    }

    public void setPreviousLocation(Cell prevCell) {
        if ((prevCell.getX() < 5 && prevCell.getX() >= 0) && (prevCell.getY() < 5 && prevCell.getY() >= 0)) {
            this.prevCell = (Block) prevCell;
        }
    }

    public void setPreviousBuild(Cell prevBuild) {
        if ((prevBuild.getX() < 5 && prevBuild.getX() >= 0) && (prevBuild.getY() < 5 && prevBuild.getY() >= 0)) {
            this.prevBuild = (Block) prevBuild;
        }
    }

    /**
     * Method that checks if the worker can be moved
     *
     * @return {@code true} if the worker can be moved, {@code false} otherwise
     */
    @Override
    public boolean isMovable() {
        return true;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    /**
     * Method that checks the gender of the worker
     *
     * @return {@code true} if the worker is a male, {@code false} if it is a female
     */
    public boolean isMale() {
        return gender;
    }
}
