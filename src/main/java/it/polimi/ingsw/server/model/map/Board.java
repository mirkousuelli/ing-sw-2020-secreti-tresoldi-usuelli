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

public class Board implements Cloneable {
    /* @class
     * it holds 25 cells in a 5x5 map
     */

    public final int DIM = 5;
    public Cell[][] map;

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */
    public Board() {
        /* @constructor
         * it builds up cell-by-cell the whole map.
         */

        this.map = new Cell[DIM][DIM];

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                this.map[i][j] = new Block(i, j);
            }
        }
    }

    /* GETTER ---------------------------------------------------------------------------------------------------------- */

    public Cell getCell(int x, int y){
        /* @getter
         * it gets a specific cell in return through xy coordinates
         */

        if ((x >= 0 && x < 5) && (y >= 0 && y < 5)) {
            return this.map[x][y];
        }

        return null;
    }

    public int getLength() {
        /* @getter
         * it returns the length of the square map
         */

        return this.DIM;
    }

    public List<Cell> getRow(int row) {
        /* @getter
         * it gets the full row selected as a list
         */

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

    public List<Cell> getColumn(int col) {
        /* @getter
         * it gets the full column selected as a list
         */

        if (col >= 0 && col < 5) {
            List<Cell> colList = new ArrayList<Cell>();

            for (int i = 0; i < this.DIM; i++) {
                // adding each cell of the same column
                colList.add(this.getCell(i, col));
            }

            return colList;
        }

        return null;
    }

    /* FUNCTION ----------------------------------------------------------------------------------------------------- */

    public List<Cell> getAround(Cell cell) {
        /* @function
         * it returns 8 cells next to the current one passed as parameter
         * --------------------------------------------------------------------
         * @params
         *          (Cell) cell : base cell where to extrapolate its own cells
         * --------------------------------------------------------------------
         * @return
         *          (ArrayList<Cell>) : 8 cells around the base passed
         */

        /* ------- EXISTENCE CONTROL ------- */
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

    public List<Cell> getSpecialMoves(Cell cell, Player player, Timing timing) {
        /* @function
         * returns all special moves that can be activated by some gods where
         * around cell are busy from other players' workers
         */

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
                        if (!mp.getAllowedAction().equals(MovementType.DEFAULT) && mp.getTiming().equals(timing))
                            toReturn.add(c);
                    }
                }
            }
        }

        return toReturn;
    }

    public List<Cell> getSpecialBuilds(Cell cell, Player player, Timing timing) {
        /* @function
         * returns all special moves that can be activated by some gods where
         * around cell are busy from other players' workers
         */

        List<Cell> around = getAround(cell).stream().filter(Cell::isWalkable).filter(Cell::isFree).collect(Collectors.toList());
        List<Cell> toReturn = new ArrayList<>();
        List<Power> activePowerList = player.getCard().getPowerList().stream().filter(power -> power.getEffect().equals(Effect.BUILD)).filter(power -> power.getTiming().equals(timing)).collect(Collectors.toList());

       if (activePowerList.stream().map(Power::getConstraints).map(Constraints::isUnderItself).distinct().reduce(false, (a, b) -> a ? true : b))
           around.add(player.getCurrentWorker().getLocation());

        /*if (activePowerList.stream().map(Power::getConstraints).map(Constraints::isSameCell).distinct().reduce(false, (a, b) -> a ? true : b)) {
            Cell prev = player.getCurrentWorker().getPreviousBuild();

            if (!around.contains(player.getCurrentWorker().getPreviousBuild()))
                around.add(prev);
        }*/

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

    public List<Cell> getPossibleMoves(Player player) {
        /* @getter
         * it considers malus attributes in player and modify possible around cells
         */
        List<Cell> toReturn = getAround(player.getCurrentWorker().getLocation());
        List<Cell> copy;

        // checking for around cell higher than allowed
        for (Cell around : getAround(player.getCurrentWorker().getLocation())) {
            // if it is busy or complete or higher than allowed
            if ((!around.isWalkable()) || (player.getCurrentWorker().getLocation().getLevel().toInt() + 1 < around.getLevel().toInt())) {
                // then remove it from the list
                toReturn.remove(around);
            }
        }

        for (Malus malus : player.getMalusList()) {
            for (MalusLevel direction : malus.getDirection()) {
                if (direction == MalusLevel.UP) {
                    for (Cell around : this.getAround(player.getCurrentWorker().getLocation())) {
                        // checking level difference
                        if (player.getCurrentWorker().getLocation().getLevel().toInt() < around.getLevel().toInt()) {
                            //removing from the list to return
                            toReturn.remove(around);
                        }
                    }
                } else if (direction == MalusLevel.DOWN) {
                    for (Cell around : this.getAround(player.getCurrentWorker().getLocation())) {
                        // checking level difference
                        if (player.getCurrentWorker().getLocation().getLevel().toInt() > around.getLevel().toInt()) {
                            //removing from the list to return
                            toReturn.remove(around);
                        }
                    }
                } else if (direction == MalusLevel.SAME) {
                    copy = new ArrayList<>(toReturn);

                    for (Cell around : this.getAround(player.getCurrentWorker().getLocation())) {
                        // checking level difference
                        if (player.getCurrentWorker().getLocation().getLevel().toInt() == around.getLevel().toInt()) {
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

        // cannot move up malus active
        /*if (this.player.cannotMoveUpActive()) {
            // look for everything around
            for (Cell around : this.currCell.getAround()) {
                // checking level difference
                if (this.currCell.getLevel().toInt() < around.getLevel().toInt()) {
                    //removing from the list to return
                    toReturn.remove(around);
                }
            }
            // if everything is removed then i will return null
        }

        // must move up malus active
        if (this.player.isMustMoveUpActive()) {
            // look for everything around
            for (Cell around : this.currCell.getAround()) {
                // checking level difference
                if (this.currCell.getLevel().toInt() > around.getLevel().toInt()) {
                    //removing from the list to return
                    toReturn.remove(around);
                }
            }

            // in case i removed everything i reset around
            if (toReturn == null) {
                toReturn = this.currCell.getAround();
            }
        }*/

        // in case no malus has been active : normal getAround()
        // in case both malus are active : normal getAround()
        return toReturn;
    }

    public List<Cell> getPossibleBuilds(Cell cell) {
        /* @getter
         * it gets possible cell where to build
         */

        // in this case, since no gods have consequence on it, it doesn't change
        List<Cell> toReturn = getAround(cell);

        for (Cell c : getAround(cell)) {
            if (!c.isFree() || c.getLevel().equals(Level.DOME))
                toReturn.remove(c);
        }

        return toReturn;
    }

    public boolean move(Player player, Cell newCell) {
        /* @function
         * it makes worker moving to another cell going through an operation of undecorate-decorate
         */
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

    public boolean build(Player player, Cell cellToBuildUp) {
        /* @function
         * it builds around except for its current location (by default), unless a god change this rule
         */
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

    public void clean() {
        /* @function
         * it cleans off the entire map setting GROUND as default level and
         * removing eventual pawns
         */

        // checking each row
        for (int i = 0; i < this.DIM; i++) {
            // and every cell in the current row
            for (Cell cell : this.getRow(i)) {
                // cleaning every cell on the map
                cell.clean();
            }
        }
    }

    @Override
    public Board clone() {
        Board cloned = new Board();

        for (int i = 0; i < this.DIM; i++) {
            cloned.map[i] = this.getRow(i).toArray(new Cell[0]);
        }

        return cloned;
    }
}
