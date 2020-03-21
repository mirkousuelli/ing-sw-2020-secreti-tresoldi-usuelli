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

    public final int DIM_X = 5;
    public final int DIM_Y = 5;
    private Cell[][] map;

    /* CONSTRUCTOR ----------------------------------------------------------------------------------------------------- */
    Board() {
        /* @constructor
         * it builds up cell-by-cell the whole map.
         */
        this.map = new Cell[DIM_X][DIM_Y];

        for (int i = 0; i < DIM_X; i++) {
            for (int j = 0; j < DIM_Y; j++) {
                this.map[i][j] = new Block(i, j);
            }
        }
    }

    /* GETTER ---------------------------------------------------------------------------------------------------------- */

    public Cell getCell(int x, int y) {
        /* @getter
         * it gets a specific cell in return through xy coordinates
         * --------------------------------------------------------------------
         * @params
         *          (int) x : column coordinate
         *          (int) y : row coordinate
         * --------------------------------------------------------------------
         * @return
         *          (Cell) : specific cell (x,y)
         */
        return this.map[x][y];
    }

    /* FUNCTION ----------------------------------------------------------------------------------------------------- */

    public ArrayList<Cell> getAround(Cell cell) {
        /* @function
         * it returns 8 cells next to the current one passed as parameter
         * --------------------------------------------------------------------
         * @params
         *          (Cell) cell : base cell where to extrapolate its own cells
         * --------------------------------------------------------------------
         * @return
         *          (ArrayList<Cell>) : 8 cells around the base passed
         */
        return null;
    }

    public void clean() {
        /* @function
         * it cleans off the entire map setting GROUND as default level and
         * removing eventual pawns
         */
    }
}
