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

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.List;

public class BuildPower<S> extends ActivePower<S> {

    public BuildPower() {
        super();
    }

    protected boolean useActivePower(Player currentPlayer, Cell cellToBuild, List<Cell> adjacency) {
        if (!constraints.isUnderItself() && !cellToBuild.isFree()) return false;
        if (constraints.isUnderItself() && cellToBuild.getLevel().equals(Level.TOP)) return false;

        if (getAllowedAction().equals(BlockType.DOME))
            return build(cellToBuild, 4);

        return build(cellToBuild, cellToBuild.getLevel().toInt() + 1);
    }

    private boolean build(Cell cellToBuild, int level) {
        Worker temp = (Worker) ((Block) cellToBuild).getPawn();

        ((Block) cellToBuild).removePawn();
        ((Block) cellToBuild).setPreviousLevel(cellToBuild.getLevel());
        workerToUse.setPreviousBuild(cellToBuild);
        cellToBuild.setLevel(Level.parseInt(level));
        ((Block) cellToBuild).addPawn(temp);

        return true;
    }

}
