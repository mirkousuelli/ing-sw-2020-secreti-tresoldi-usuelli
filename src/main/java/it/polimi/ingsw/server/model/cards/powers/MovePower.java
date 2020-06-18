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
        if (cellToMove.getLevel().toInt() - workerToUse.getLocation().getLevel().toInt() > 1) return false;

        if (getAllowedAction().equals(MovementType.SWAP)) {
            if (cellToMove.isFree()) return false;

            Block opponentCellToMove = workerToUse.getLocation();
            Worker opponentWorker = ((Worker) ((Block) cellToMove).getPawn());

            return move(currentPlayer, cellToMove, opponentWorker, opponentCellToMove);
        }
        else if (getAllowedAction().equals(MovementType.PUSH))
            return push(currentPlayer, cellToMove, adjacency);
        else
            return move(currentPlayer, cellToMove, null, null);
    }

    private boolean push(Player currentPlayer, Cell cellToMove, List<Cell> adjacency) {
        if (cellToMove.isFree()) return false;
        if (workerToUse.getLocation().equals(cellToMove)) return false; //you cannot push yourself!

        Cell workerToPushNewPos = MovePower.lineEqTwoPoints(workerToUse.getLocation(), cellToMove); //new position of the worker to push, it is a shallow copy, it's noy the original from the board
        if (workerToPushNewPos == null) return false; //happens when the worker to push is on a perimeter cell and it is pushed overboard

        Block opponentCellToMove = (Block) findCell(adjacency, workerToPushNewPos.getX(), workerToPushNewPos.getY()); //gets the new position of the worker to push in the board
        Worker opponentWorker = ((Worker) ((Block) cellToMove).getPawn());

        if (opponentCellToMove == null) return false; //verifies that the worker to push is push only of a unit
        if (!opponentCellToMove.isWalkable()) return false; //cannot push the worker to push onto a non walkable cell (occupied or dome-level cell)

        return move(currentPlayer, cellToMove, opponentWorker, opponentCellToMove);
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
     * @param opponentCellToMove the new block where the opponent's worker is moved
     * @return {@code true} after the move is complete, {@code false} if the chosen cell has no opponent's worker
     */
    private boolean move(Player currentPlayer, Cell cellToMove, Worker opponentWorker, Block opponentCellToMove) {
        if (currentPlayer.getWorkers().contains(opponentWorker)) return false;
        if (opponentWorker == null && !cellToMove.isFree()) return false;

        workerToUse.setPreviousLocation(workerToUse.getLocation());
        workerToUse.setLocation((Block) cellToMove);

        if (opponentWorker != null) {
            opponentWorker.setPreviousLocation(opponentWorker.getPreviousLocation());
            opponentWorker.setLocation(opponentCellToMove);
            ((Block) cellToMove).addPawn(workerToUse);
        }
        else if (constraints.isPerimCell() && !Cell.isPerim(cellToMove) && numberOfActionsRemaining == -1)
            numberOfActionsRemaining = 0;

        return true;
    }

    /**
     * Method that identifies the new cell of the opponent's worker
     *
     * @param list list of cells where the cell can be
     * @param x x-coordinate of the cell to be found
     * @param y y-coordinate of the cell to be found
     * @return the cell where the opponent's worker is
     */
    public Cell findCell(List<Cell> list, int x, int y) {
        for (Cell c: list){
            if (c.getX() == x && c.getY() == y && c.isFree())
                return c;
        }

        return null;
    }

    public static Cell lineEqTwoPoints(Cell from, Cell to) {
        if (from == null) return null;
        if (to == null) return null;
        if (to.getX() == from.getX() && to.getY() == from.getY()) return null; //'from' and 'to' cannot be the same cell!

        if (to.getX() != from.getX()) { //y = mx + q (slope-intercept)
            float m = ((float) (to.getY() - from.getY())) / ((float) (to.getX() - from.getX())); //slope
            float q = from.getY() - m*from.getX(); //intercept

            return MovePower.fetchNextCell(from, to, m ,q);
        }
        else { //x = k (vertical line)
            int y;
            if (from.getY() > to.getY())
                y = to.getY() - 1;
            else
                y = to.getY() + 1;

            if (y >= 0 && y <= 4)
                return new Block(to.getX(), y);
            else
                return null;
        }
    }

    public static Cell fetchNextCell(Cell from, Cell to, float m, float q) {
        int newX;

        if (from.getX() < to.getX())
            newX = to.getX() + 1;
        else
            newX = to.getX() - 1;

        if (newX >= 0 && newX <= 4) {
            int newY = (int) (m*newX + q);

            if (newY >= 0 && newY <= 4)
                return new Block(newX, newY);
            else
                return null;
        }
        else
            return null;
    }
}
