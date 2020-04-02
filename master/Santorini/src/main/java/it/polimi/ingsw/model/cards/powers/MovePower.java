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
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;
import it.polimi.ingsw.model.map.Worker;

import java.util.List;

public class MovePower extends ActivePower {

    public MovePower(Card card) {
        super(card);
    }

    @Override
    protected boolean useActivePower(Cell cellToMove) {
        if (constraints.isSameCell() && !cellToMove.equals(workerToUse.getPreviousLocation())) return false;
        if (constraints.isNotSameCell() && cellToMove.equals(workerToUse.getPreviousLocation())) return false;

        Block newPos;
        Worker opponentWorker = ((Worker) ((Block) cellToMove).getPawn());

        if (allowedMove.equals(MovementType.SWAP)) {
            newPos = (Block) workerToUse.getLocation();

            return move(cellToMove, opponentWorker, newPos);
        }
        else if (allowedMove.equals(MovementType.PUSH)) {
            newPos = (Block) find(cellToMove);

            if (newPos == null) return false;
            if (!newPos.isFree()) return false;
            if (newPos.getLevel().equals(Level.DOME)) return false;

            return move(cellToMove, opponentWorker, newPos);
        }
        else {
            return card.getOwner().move(cellToMove);
        }
    }

    private boolean move(Cell cellToMove, Worker opponentWorker, Block newPos) {
        if (cellToMove.isFree()) return false;
        if (opponentWorker == null) return false;
        if (opponentWorker.getPlayer().equals(card.getOwner())) return false;

        workerToUse.setPreviousLocation(workerToUse.getLocation());
        opponentWorker.setPreviousLocation(cellToMove);

        ((Block) workerToUse.getLocation()).removePawn();
        opponentWorker.setLocation(newPos);
        workerToUse.setLocation((Block) cellToMove);
        ((Block) opponentWorker.getLocation()).addPawn(opponentWorker);

        return true;
    }

    private Cell find(Cell cell) {
        /*@function
         * it identifies the direction in which the opponent's worker will be forced to move
         */

        List<Cell> adjacency = ((Block) cell).getAround();
        Cell currCell = card.getOwner().getCurrentWorker().getLocation();

        if (currCell.getX() < cell.getX()) {
            if (currCell.getY() < cell.getY())
                return findCell(adjacency, cell.getX() + 1, cell.getY() + 1);
            else if (currCell.getY() > cell.getY())
                return findCell(adjacency, cell.getX() + 1, cell.getY() - 1);
            else
                return findCell(adjacency, cell.getX() + 1, cell.getY());
        }
        else if (currCell.getX() > cell.getX()) {
            if (currCell.getY() < cell.getY())
                return findCell(adjacency, cell.getX() - 1, cell.getY() + 1);
            else if (currCell.getY() > cell.getY())
                return findCell(adjacency, cell.getX() - 1, cell.getY() - 1);
            else
                return findCell(adjacency, cell.getX() - 1, cell.getY());
        }
        else {
            if (currCell.getY() < cell.getY())
                return findCell(adjacency, cell.getX() , cell.getY() + 1);
            else if (currCell.getY() > cell.getY())
                return findCell(adjacency, cell.getX() , cell.getY() - 1);
        }

        return null;
    }

    private Cell findCell(List<Cell> list, int x, int y) {
        /*@function
         * it identifies the new cell of the opponent's worker
         */

        for (Cell c: list){
            if (c.getX() == x && c.getY() == y)
                return c;
        }


        return null;
    }
}
