/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.map;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Constraints;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.MovementType;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class that represents a board, which contains 25 cells in a 5x5 map
 */
public class Board implements Cloneable {

    public final int DIM = 5;
    public Cell[][] map;

    /**
     * Constructor of the board, that builds up cell-by-cell the whole map
     */
    public Board() {

        this.map = new Cell[DIM][DIM];

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                this.map[i][j] = new Block(i, j);
            }
        }
    }

    public Cell getCell(int x, int y){
        if ((x >= 0 && x < 5) && (y >= 0 && y < 5)) {
            return this.map[x][y];
        }

        return null;
    }

    public int getLength() {
        return this.DIM;
    }

    public List<Cell> getRow(int row) {
        if (row >= 0 && row < 5) {
            List<Cell> rowList = new ArrayList<Cell>();

            for (int i = 0; i < this.DIM; i++) {
                // adding each cell of the same row
                rowList.add(this.getCell(row, i));
            }

            return rowList;
        }

        return null;
    }

    /**
     * Method that gets the 8 cells around the one passed as parameter
     *
     * @param cell the cell from which the cells around are calculated
     * @return list of cells around the chosen one
     */
    public List<Cell> getAround(Cell cell) {

        boolean notExists = true;
        int i;

        i = 0;
        while (i < this.DIM && notExists) {
            notExists = Objects.equals(this.getRow(i), cell);
            i++;
        }

        if (!notExists) {
            // creating the array to pass in return
            List<Cell> around = new ArrayList<Cell>();

            // creating a set of x around parameters to sum in iteration
            List<Integer> xCheck = new ArrayList<Integer>() {{
                add(1);
                add(0);
                add(-1);
            }};

            // creating a set of y around parameters to sum in iteration
            List<Integer> yCheck = new ArrayList<Integer>() {{
                add(1);
                add(0);
                add(-1);
            }};

            // calling x and y just once
            int x = cell.getX();
            int y = cell.getY();

            // for all x around checks
            for (Integer xAround : xCheck) {
                // for all y around checks
                for (Integer yAround : yCheck) {
                    // i avoid the current cell control (x + 0, y + 0)
                    if (!(xAround.equals(0) && yAround.equals(0))) {
                        // look for border condition adding null obj in case
                        if (!(x + xAround < 0 || x + xAround == this.DIM || y + yAround < 0 || y + yAround == this.DIM)) {
                            around.add(this.getCell(x + xAround, y + yAround));
                        }
                    }
                }
            }

            return around;
        }

        return null;
    }

    /**
     * Method that gets all the special moves that can be activated by some Gods
     *
     * @param cell the cell from which the cells for special moves are calculated
     * @param player the player who can make the eventual special move
     * @param timing the timing of the power, depending from the God
     * @return list of cells where a special move is possible
     */
    public List<Cell> getSpecialMoves(Cell cell, Player player, Timing timing) {

        List<Cell> around = getAround(cell);
        List<Cell> toReturn = new ArrayList<>();
        List<Power> activePowerList = player.getCard().getPowerList().stream().filter(power -> power.getEffect().equals(Effect.MOVE)).filter(power -> power.getTiming().equals(timing)).collect(Collectors.toList());

        for (Cell c : around) {
            for (Power mp : activePowerList) {
                if (((MovePower) mp).preamble(player, c)) {
                    if (c.isFree()) {
                        if (mp.getAllowedAction().equals(MovementType.DEFAULT) && mp.getTiming().equals(timing))
                            toReturn.add(c);
                    }
                    else if (!player.getWorkers().contains((Worker) (((Block) c).getPawn()))) {
                        if (!mp.getAllowedAction().equals(MovementType.DEFAULT) && mp.getTiming().equals(timing)) {
                            if (mp.getAllowedAction().equals(MovementType.PUSH)) {
                                Cell nc = MovePower.lineEqTwoPoints(player.getCurrentWorker().getLocation(), c);
                                if (nc != null) {
                                    nc = getCell(nc.getX(), nc.getY());
                                    if (nc.isFree() && !nc.getLevel().equals(Level.DOME) && c.getLevel().toInt() - player.getCurrentWorker().getLocation().getLevel().toInt() <= 1)
                                        toReturn.add(c);
                                }
                            }
                            else
                                toReturn.add(c);
                        }
                    }
                }
            }
        }

        toReturn.removeIf(c -> c.getLevel().toInt() > player.getCurrentWorker().getLocation().getLevel().toInt() + 1);

        return toReturn;
    }

    /**
     * Method that gets all the special builds that can be activated by some God powers, where around cell are busy
     * from other players' workers
     *
     * @param cell the cell from which the cells for special build are calculated
     * @param player the player who can make the eventual special build
     * @param timing the timing of the power, that depends from the God
     * @return list of cells where a special build is possible
     */
    public List<Cell> getSpecialBuilds(Cell cell, Player player, Timing timing) {

        List<Cell> around = getAround(cell).stream().filter(Cell::isWalkable).filter(Cell::isFree).collect(Collectors.toList());
        List<Cell> toReturn = new ArrayList<>();
        List<Power> activePowerList = player.getCard().getPowerList().stream().filter(power -> power.getEffect().equals(Effect.BUILD)).filter(power -> power.getTiming().equals(timing)).collect(Collectors.toList());

        if (activePowerList.stream().map(Power::getConstraints).map(Constraints::isUnderItself).distinct().reduce(false, (a, b) -> a ? true : b))
            around.add(player.getCurrentWorker().getLocation());

        for (Cell c : around) {
            for (Power bp : activePowerList) {
                if (((BuildPower) bp).preamble(player, c)) {
                    if (!(bp.getAllowedAction().equals(BlockType.NOT_DOME) && c.getLevel().equals(Level.TOP)))
                        toReturn.add(c);
                }
            }
        }

        return toReturn;
    }

    /**
     * Method that gets then cell where the player can move to
     * <p>
     * It takes all the cells around the current location and removes the one where the player cannot move, considering
     * all rules of moving and possible maluses
     *
     * @param player Player that has to move
     * @return list of cells where the player can move
     */
    public List<Cell> getPossibleMoves(Player player) {
        Cell workerLocation = player.getCurrentWorker().getLocation();
        List<Cell> toReturn = getAround(workerLocation);
        List<Cell> copy;

        // checking for around cell higher than allowed
        for (Cell around : getAround(workerLocation)) {
            // if it is busy or complete or higher than allowed
            if ((!around.isWalkable()) || (workerLocation.getLevel().toInt() + 1 < around.getLevel().toInt())) {
                // then remove it from the list
                toReturn.remove(around);
            }
        }

        for (Malus malus : player.getMalusList()) {
            for (MalusLevel direction : malus.getDirection()) {
                if (direction == MalusLevel.UP) {
                    for (Cell around : this.getAround(workerLocation)) {
                        // checking level difference
                        if (workerLocation.getLevel().toInt() < around.getLevel().toInt()) {
                            //removing from the list to return
                            toReturn.remove(around);
                        }
                    }
                } else if (direction == MalusLevel.DOWN) {
                    for (Cell around : this.getAround(workerLocation)) {
                        // checking level difference
                        if (workerLocation.getLevel().toInt() > around.getLevel().toInt()) {
                            //removing from the list to return
                            toReturn.remove(around);
                        }
                    }
                } else if (direction == MalusLevel.SAME) {
                    copy = new ArrayList<>(toReturn);

                    for (Cell around : this.getAround(workerLocation)) {
                        // checking level difference
                        if (workerLocation.getLevel().toInt() == around.getLevel().toInt()) {
                            //removing from the list to return
                            toReturn.remove(around);
                        }
                    }

                    if (toReturn.size() == 0) {
                        toReturn = copy;
                    }
                }
            }
        }

        // in case no malus has been active : normal getAround()
        // in case both malus are active : normal getAround()
        return toReturn;
    }

    /**
     * Method that gets then cell where the player can build
     * <p>
     * It takes all the cells around the given cell and removes the one where the player cannot build, that are the
     * ones that are occupied by a worker or have a dome
     *
     * @param cell the cell from which it calculates the possible builds
     * @return list of cells where the player can build
     */
    public List<Cell> getPossibleBuilds(Cell cell) {

        // in this case, since no gods have consequence on it, it doesn't change
        List<Cell> toReturn = getAround(cell);

        for (Cell c : getAround(cell)) {
            if (!c.isFree() || c.getLevel().equals(Level.DOME))
                toReturn.remove(c);
        }

        return toReturn;
    }

    /**
     * Method that makes the worker move to the chosen cell, through an operation of undecorate-decorate
     *
     * @param player Player that is requiring the move
     * @param newCell Cell where the worker wants to be moved
     * @return {@code true} if the worker is moved properly, {@code false} if the action wasn't possible
     */
    public boolean move(Player player, Cell newCell) {

        Worker worker = player.getCurrentWorker();

        // if it is not a dome, free and it is contained within possible choices
        if (newCell.isWalkable() && this.getPossibleMoves(player).contains(newCell)) {
            // removing pawn from the current cell
            worker.getLocation().removePawn();

            // updating previous cell with the old current cell
            worker.setPreviousLocation((worker.getLocation()));

            // updating current cell with the new cell just moved on
            worker.setLocation((Block) newCell);

            // returning everything correct
            return true;
        }

        // try again
        return false;
    }

    /**
     * Method that makes the worker build on the chosen cell
     *
     * @param player Player that is requiring the build
     * @param cellToBuildUp Cell where the worker wants to build
     * @return {@code true} if the build is made properly, {@code false} if the action wasn't possible
     */
    public boolean build(Player player, Cell cellToBuildUp) {
        Worker worker = player.getCurrentWorker();
        Block toBuild = (Block) cellToBuildUp;

        // if it is not a dome, free and it is contained within possible choices
        if (toBuild.isWalkable() && this.getPossibleBuilds(worker.getLocation()).contains(toBuild)) {
            // storing previous level
            toBuild.setPreviousLevel(toBuild.getLevel());

            // then build it up
            toBuild.setLevel(toBuild.getLevel().buildUp());

            // updating last building
            worker.setPreviousBuild(toBuild);

            // returning everything correct
            return true;
        }

        // try again
        return false;
    }

    /**
     * Method that cleans off the entire map, setting the default level of each cell to ground and removing every pawn
     */
    public void clean() {
        // checking each row
        for (int i = 0; i < this.DIM; i++) {
            // and every cell in the current row
            for (Cell cell : this.getRow(i)) {
                // cleaning every cell on the map
                cell.clean();
            }
        }
    }

    /**
     * Method that clones the board of the game that is being played
     *
     * @return the cloned board
     */
    @Override
    public Board clone() {
        Board cloned = new Board();

        for (int i = 0; i < this.DIM; i++) {
            cloned.map[i] = this.getRow(i).toArray(new Cell[0]);
        }

        return cloned;
    }
}
