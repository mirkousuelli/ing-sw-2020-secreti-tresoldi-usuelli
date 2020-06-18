/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.communication.message.payload.ILevel;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.List;

/**
 * Class that represents a build power
 * <p>
 * It extends {@link ActivePower}
 */
public class BuildPower<S> extends ActivePower<S> {

    /**
     * Constructor of the build power that recalls its super class {@link ActivePower}
     */
    public BuildPower() {
        super();
    }

    /**
     * Method that builds on the chosen cell, considering every case of different God powers
     *
     * @param currentPlayer the player that uses the power
     * @param cellToBuild the chosen cell to build on
     * @param adjacency list of cells around the worker
     * @return {@code true} if the power is used correctly, {@code false} otherwise
     */
    @Override
    protected boolean useActivePower(Player currentPlayer, Cell cellToBuild, List<Cell> adjacency) {
        if (!constraints.isUnderItself() && !cellToBuild.isFree()) return false;
        if (constraints.isUnderItself() && cellToBuild.getLevel().equals(Level.TOP)) return false;
        if (!adjacency.contains(workerToUse.getLocation()) && !cellToBuild.equals(workerToUse.getLocation())) return false;

        if (getAllowedAction().equals(BlockType.DOME))
            return build(cellToBuild, 4);
        else if (getAllowedAction().equals(BlockType.NOT_DOME) && cellToBuild.getLevel().equals(Level.TOP))
            return false;

        return build(cellToBuild, cellToBuild.getLevel().toInt() + 1);
    }

    /**
     * Method that builds on the chosen cell at the designated level. It is used for Atlas, who can decide to build a
     * dome at any level
     *
     * @param cellToBuild chosen cell to build on
     * @param level level that wants to be built
     * @return {@code true} after the build is complete
     */
    private boolean build(Cell cellToBuild, int level) {
        Worker temp = (Worker) ((Block) cellToBuild).getPawn();

        ((Block) cellToBuild).removePawn();
        ((Block) cellToBuild).setPreviousLevel(cellToBuild.getLevel());
        workerToUse.setPreviousBuild(cellToBuild);
        cellToBuild.setLevel((Level) ILevel.parseInt(level));
        ((Block) cellToBuild).addPawn(temp);

        return true;
    }

}
