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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void getAroundTest() {
        Board tester = new Board();
        List<Cell> around;

        // for each row in the map
        for (int i = 0; i < tester.getLength(); i++) {

            // i check each cell surroundings
            for (Cell cell : tester.getRow(i)) {

                // getting around cells
                around = tester.getAround(cell);

                // checking if i got an array of 8 cells or less (angle condition)
                assertTrue(around.size() <= 8);

                // analysing each cell around the chosen one
                for (Cell next : around) {
                    // checking that all cells around are distant of 1 unit for x or for y, or
                    // in case the current cell is a border one if around have null
                    assertTrue(next == null ||
                            ((cell.getX() == next.getX() - 1 || cell.getX() == next.getX() + 1) &&
                            (cell.getY() == next.getY() - 1 || cell.getY() == next.getY() + 1 )));

                    // analysing if around cells are different from one another (except for null)
                    for (Cell other : around) {
                        if ((next != other) && (next != null)) {
                            assertNotEquals(next, other);
                        }
                    }
                }
            }
        }
    }

    @Test
    void cleanTest() {
        Board tester = new Board();

        tester.clean();

        for (int i = 0; i < tester.getLength(); i++) {
            for (Cell cell : tester.getRow(i)) {
                // checking if every cells now are free and at ground level
                assertTrue(!cell.isBusy() && cell.getLevel() == Level.GROUND);
            }
        }
    }

}