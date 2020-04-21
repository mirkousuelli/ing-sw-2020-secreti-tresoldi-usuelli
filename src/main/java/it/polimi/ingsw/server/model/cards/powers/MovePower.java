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
import it.polimi.ingsw.server.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.List;

public class MovePower<S> extends ActivePower<S> {

    public MovePower() {
        super();
    }

    @Override
    protected boolean useActivePower(Player currentPlayer, Cell cellToMove, List<Cell> adjacency) {
        if (constraints.isSameCell() && !cellToMove.equals(workerToUse.getPreviousLocation())) return false;
        if (constraints.isNotSameCell() && cellToMove.equals(workerToUse.getPreviousLocation())) return false;

        Block newPos;
        Worker opponentWorker = ((Worker) ((Block) cellToMove).getPawn());

        if (getAllowedAction().equals(MovementType.SWAP)) {
            newPos = workerToUse.getLocation();

            return move(currentPlayer, cellToMove, opponentWorker, newPos);
        }
        else if (getAllowedAction().equals(MovementType.PUSH)) {
            newPos = (Block) find(currentPlayer, cellToMove, adjacency);

            if (newPos == null) return false;
            if (!newPos.isFree()) return false;
            if (newPos.getLevel().equals(Level.DOME)) return false;

            return move(currentPlayer, cellToMove, opponentWorker, newPos);
        }
        else {
            if (!cellToMove.isFree()) return false;
            if (cellToMove.getLevel().toInt() - workerToUse.getLocation().getLevel().toInt() >= 2) return false;

            workerToUse.setPreviousLocation(workerToUse.getLocation());
            workerToUse.getLocation().removePawn();
            workerToUse.setLocation((Block) cellToMove);

            return true;
        }
    }

    private boolean move(Player currentPlayer, Cell cellToMove, Worker opponentWorker, Block newPos) {
        if (cellToMove.isFree()) return false;
        if (opponentWorker == null) return false;
        if (opponentWorker.getPlayer().equals(currentPlayer)) return false;

        workerToUse.setPreviousLocation(workerToUse.getLocation());
        opponentWorker.setPreviousLocation(cellToMove);

        workerToUse.getLocation().removePawn();
        opponentWorker.setLocation(newPos);
        workerToUse.setLocation((Block) cellToMove);
        opponentWorker.getLocation().addPawn(opponentWorker);

        return true;
    }

    private Cell find(Player currentPlayer, Cell cell, List<Cell> adjacency) {
        /*@function
         * it identifies the direction in which the opponent's worker will be forced to move
         */

        Cell currCell = currentPlayer.getCurrentWorker().getLocation();

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

    @Override
    public String toString() {
        return "MOVE";
    }
}
