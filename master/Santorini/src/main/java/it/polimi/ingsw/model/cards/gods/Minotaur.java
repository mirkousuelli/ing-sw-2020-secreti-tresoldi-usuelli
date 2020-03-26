/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.exceptions.cards.OutOfBorderException;
import it.polimi.ingsw.model.exceptions.cards.CompleteTowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongCellException;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;
import it.polimi.ingsw.model.map.Worker;

import java.util.List;

/*Power:
 *  Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards
 *  to an unoccupied space at any level
 */

public class Minotaur extends Card {
    /*@class
     * it portrays the power of Minotaur
     */

    public Minotaur() {
        /*@constructor
         * it calls the constructor of the superclass
         */

        super(God.MINOTAUR, Effect.MOVE);
    }

    @Override
    public void usePower(Cell cell) throws Exception/*,NullPointerException, WrongWorkerException, OccupiedCellException, CompleteTowerException, WrongCellException*/ {
        /*@function
         * it implements Minotaur's power
         */

        if (cell == null) throw new NullPointerException("Cell is null!");

        Cell newPos = find(cell);

        //if (newPos.equals(cell)) throw new WrongWorkerException("It's the same worker!");
        if (newPos == null) throw new OutOfBorderException("Cannot push a worker out of the board!");
        if (!newPos.isFree()) throw new WrongCellException("Wrong cell!");
        if (newPos.getLevel().equals(Level.DOME)) throw new CompleteTowerException("Cannot Move onto a dome!");
        if (((Block) cell).getPawn().getPlayer().equals(getOwner())) throw new WrongWorkerException("It's your worker! You can't...");

        //swap
        ((Worker) ((Block) cell).getPawn()).moveTo(newPos);
        getOwner().getCurrentWorker().setPreviousLocation(getOwner().getCurrentWorker().getLocation());
        getOwner().getCurrentWorker().setLocation((Block) cell);
        ((Block) cell).addPawn(getOwner().getCurrentWorker());
    }

    private Cell find(Cell cell) {
        /*@function
         * it identifies the direction in which the opponent's worker will be forced to move
         */

        List<Cell> adjacency = ((Block) cell).getAround();
        Cell currCell = getOwner().getCurrentWorker().getLocation();

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

        /*for (Cell c : adjacency) {
            if ((c.getX() - cell.getX()) == (c.getY() - cell.getY())) return findCell(adjacency, cell.getX() + 1, cell.getY() + 1);
            if ((c.getX() - cell.getX() == 0) && (c.getY() - cell.getY() != 0)) return findCell(adjacency, cell.getX(), cell.getY() + 1);
            if ((c.getX() - cell.getX() != 0) && (c.getY() - cell.getY() == 0)) return findCell(adjacency, cell.getX() + 1, cell.getY());
        }*/

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
    public void usePower(List<Player> opponents) throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public boolean usePower() throws UnusedPowerException{
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }
}