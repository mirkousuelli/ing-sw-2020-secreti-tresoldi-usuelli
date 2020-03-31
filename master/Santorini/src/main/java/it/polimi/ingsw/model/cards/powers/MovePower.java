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
import it.polimi.ingsw.model.exceptions.cards.*;
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
    protected void useActivePower(Cell cellToMove) throws Exception {
        if (constraints.isSameCell()) {
            if (!cellToMove.equals(workerToUse.getPreviousLocation())) throw new WrongCellException("Not same cell!");
        }

        if (constraints.isNotSameCell()) {
            if (cellToMove.equals(workerToUse.getPreviousLocation())) throw new WrongCellException("Same cell!");
        }

        Worker opponentWorker = ((Worker) ((Block) cellToMove).getPawn());

        //basic
        if (cellToMove.isFree()) throw new EmptyCellException("Empty cell!");
        if (opponentWorker.getPlayer().equals(card.getOwner())) throw new WrongWorkerException("It's your worker! You can't...");

        //verify allowed move
        if (allowedMove.equals(MovementType.SWAP)) {
            Block temp = (Block) workerToUse.getLocation();

            //swap
            workerToUse.setPreviousLocation(workerToUse.getLocation());
            opponentWorker.setPreviousLocation(cellToMove);

            ((Block) workerToUse.getLocation()).removePawn();
            opponentWorker.setLocation(temp);
            workerToUse.setLocation((Block) cellToMove);
            ((Block) opponentWorker.getLocation()).addPawn(opponentWorker);

        }
        else if (allowedMove.equals(MovementType.PUSH)) {
            Block newPos = (Block) find(cellToMove);

            if (newPos == null) throw new OutOfBorderException("Cannot push a worker out of the board!");
            if (!newPos.isFree()) throw new WrongCellException("Wrong cell!");
            if (newPos.getLevel().equals(Level.DOME)) throw new CompleteTowerException("Cannot Move onto a dome!");

            //push
            opponentWorker.moveTo(newPos);

            workerToUse.setPreviousLocation(workerToUse.getLocation());
            workerToUse.setLocation((Block) cellToMove);
        }
        else {
            //card.getOwner().move(cellToMove);
            workerToUse.setPreviousLocation(workerToUse.getLocation());
            ((Block) (workerToUse.getLocation())).removePawn();
            workerToUse.setLocation((Block) cellToMove);
        }


        constraints.setNumberOfAdditional(constraints.getNumberOfAdditional() - 1);
    }

    private Cell find(Cell cell) throws Exception {
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
