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
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.MovementType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.List;

/**
 * Class that represents a move power
 * <p>
 * It extends {@link ActivePower}
 */
public class MovePower<S> extends ActivePower<S> {

    /**
     * Constructor of the move power that recalls its super class {@link ActivePower}
     */
    public MovePower() {
        super();
    }

    /**
     * Method that allows the player to move to the chosen cell as an active power, considering every case of
     * different God powers
     *
     * @param currentPlayer the player that uses the power
     * @param cellToMove the chosen cell to move to
     * @param adjacency list of cells around the worker
     * @return {@code true} if the power is used correctly, {@code false} otherwise
     */
    @Override
    protected boolean useActivePower(Player currentPlayer, Cell cellToMove, List<Cell> adjacency) {
        if (cellToMove.getLevel().toInt() - currentPlayer.getCurrentWorker().getLocation().getLevel().toInt() > 1) return false;

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

            if (constraints.isPerimCell() && !isPerim(cellToMove) && numberOfActionsRemaining == -1) numberOfActionsRemaining = 0;

            return true;
        }
    }

    /**
     * Method that allows the current player to move to the designated cell, while it is occupied by an opponent's
     * worker who is moved accordingly (who, depending on the current player's God power, is pushed back or swapped
     * with the worker that is moving)
     * <p>
     * This method is used only for Minotaur and Apollo, whose power allows this kind of movement
     *
     * @param currentPlayer the current player
     * @param cellToMove the chosen cell where to move
     * @param opponentWorker the worker that is pushed or swapped
     * @param newPos the new block where the opponent's worker is moved
     * @return {@code true} after the move is complete, {@code false} if the chosen cell has no opponent's worker
     */
    private boolean move(Player currentPlayer, Cell cellToMove, Worker opponentWorker, Block newPos) {
        if (cellToMove.isFree()) return false;
        if (opponentWorker == null) return false;

        for (Worker w : currentPlayer.getWorkers()) {
            if (w.equals(opponentWorker))
                return false;
        }

        workerToUse.setPreviousLocation(workerToUse.getLocation());
        opponentWorker.setPreviousLocation(cellToMove);

        workerToUse.getLocation().removePawn();
        opponentWorker.setLocation(newPos);
        workerToUse.setLocation((Block) cellToMove);
        opponentWorker.getLocation().addPawn(opponentWorker);

        return true;
    }


    /**
     * Method that identifies the direction where the opponent's worker will be forced to move
     *
     * @param currentPlayer the current player
     * @param cell the cell where to move
     * @param adjacency the list of cells around the worker
     * @return the cell where the opponent's worker is forced to move
     */
    private Cell find(Player currentPlayer, Cell cell, List<Cell> adjacency) {

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

    /**
     * Method that identifies the new cell of the opponent's worker
     *
     * @param list list of cells where the cell can be
     * @param x x-coordinate of the cell to be found
     * @param y y-coordinate of the cell to be found
     * @return the cell where the opponent's worker is
     */
    private Cell findCell(List<Cell> list, int x, int y) {
             for (Cell c: list){
            if (c.getX() == x && c.getY() == y)
                return c;
        }

        return null;
    }
}
