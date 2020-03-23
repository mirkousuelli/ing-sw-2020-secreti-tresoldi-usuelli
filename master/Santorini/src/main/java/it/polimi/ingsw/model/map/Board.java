/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.map;

import java.util.ArrayList;
import java.util.List;

public class Board {
    /* @class
     * it holds 25 cells in a 5x5 map
     */

    public final int DIM = 5;
    private Cell[][] map;

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

    public Cell getCell(int x, int y) {
        /* @getter
         * it gets a specific cell in return through xy coordinates
         */

        return this.map[x][y];
    }

    public int getLength() {
        /* @getter
         * it returns the length of the square map
         */

        return this.DIM;
    }

    public List<Cell> getRow(int row) {
        /* @getter
         *
         */

        List<Cell> rowList = new ArrayList<Cell>();

        for (int i = 0; i < this.DIM; i++) {
            // adding each cell of the same row
            rowList.add(this.getCell(row, i));
        }

        return rowList;
    }

    public List<Cell> getColumn(int col) {
        /* @getter
         *
         */

        List<Cell> colList = new ArrayList<Cell>();

        for (int i = 0; i < this.DIM; i++) {
            // adding each cell of the same column
            colList.add(this.getCell(i, col));
        }

        return colList;
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
                if (xAround != 0 && yAround != 0) {
                    // look for border condition adding null obj in case
                    if (x + xAround < 0 || x + xAround == this.DIM || y + yAround < 0 || y + yAround == this.DIM) {
                        around.add(null);
                    } else { // otherwise i add the proper cell around it
                        around.add(this.getCell(x + xAround, y + yAround));
                    }
                }
            }
        }

        return around;
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
}
