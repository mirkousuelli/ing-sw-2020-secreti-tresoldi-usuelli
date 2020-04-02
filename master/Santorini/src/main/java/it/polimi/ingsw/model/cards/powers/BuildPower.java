/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;
import it.polimi.ingsw.model.map.Worker;

public class BuildPower extends ActivePower {

    public BuildPower(Card card) {
        super(card);
    }

    @Override
    protected boolean useActivePower(Cell cellToBuild) {
        if (constraints.isSameCell() && !cellToBuild.equals(workerToUse.getPreviousBuild())) return false;
        if (constraints.isNotSameCell() && cellToBuild.equals(workerToUse.getPreviousBuild())) return false;
        if (!constraints.isUnderItself() && !cellToBuild.isFree()) return false;

        if (allowedBlock.equals(BlockType.DOME)) {
            return build(cellToBuild, 4);
        }
        else if (allowedBlock.equals(BlockType.NOT_DOME)) {
            if (cellToBuild.getLevel().equals(Level.TOP)) return false;
        }

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
