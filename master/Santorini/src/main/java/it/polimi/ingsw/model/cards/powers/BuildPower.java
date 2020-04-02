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
import it.polimi.ingsw.model.exceptions.cards.CompleteTowerException;
import it.polimi.ingsw.model.exceptions.cards.TopLevelTowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;

public class BuildPower extends ActivePower {

    public BuildPower(Card card) {
        super(card);
    }

    @Override
    protected void useActivePower(Cell cellToBuild) throws Exception {
        if (constraints.isSameCell()) {
            if (!cellToBuild.equals(workerToUse.getPreviousBuild()))
                throw new WrongCellException("Not same cell!");
        }

        if (constraints.isNotSameCell()) {
            if (cellToBuild.equals(workerToUse.getPreviousBuild()))
                throw new WrongCellException("Same cell!");
        }

        //basic
        if (cellToBuild.isComplete()) throw new CompleteTowerException("There is already a dome!");

        //verify allowed move
        if (allowedBlock.equals(BlockType.DOME)) {
            if (!cellToBuild.isFree()) throw new OccupiedCellException("Cell is occupied!");

            ((Block) cellToBuild).setPreviousLevel(cellToBuild.getLevel());
            workerToUse.setPreviousBuild(cellToBuild);
            cellToBuild.setLevel(Level.DOME);

        }
        else if (allowedBlock.equals(BlockType.NOT_DOME)) {
            if (!cellToBuild.equals(workerToUse.getLocation()) && constraints.isUnderItself()) throw new WrongCellException("Can build only under itself");
            if (cellToBuild.getLevel().equals(Level.TOP)) throw new TopLevelTowerException("Cannot build a dome!");

            //card.getOwner().build(cellToBuild);
            ((Block) cellToBuild).removePawn();
            ((Block) cellToBuild).setPreviousLevel(cellToBuild.getLevel());
            workerToUse.setPreviousBuild(cellToBuild);
            cellToBuild.setLevel(Level.parseInt(cellToBuild.getLevel().toInt() + 1));
            ((Block) cellToBuild).addPawn(workerToUse);
        }
        else
            card.getOwner().build(cellToBuild);


        constraints.setNumberOfAdditional(constraints.getNumberOfAdditional() - 1);
    }
}
