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

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void initTest() {
        /* test for correct grill initialization
         */
        Board tester = new Board();

        // checking the correct initialization
        for (int i = 0; i < tester.getLength(); i++) {
            for (int j = 0; j < tester.getLength(); j++) {
                //checking for the x
                assertEquals(i, tester.getCell(i, j).getX());

                //checking for the y
                assertEquals(j, tester.getCell(i, j).getY());

                //checking for the level
                assertEquals(Level.GROUND, tester.getCell(i, j).getLevel());
            }
        }
    }

    @Test
    void getAroundTest() {
        /* test for correct around cells list usage
         */
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
                    assertTrue(((cell.getX() == next.getX() - 1 || cell.getX() == next.getX() + 1 || cell.getX() == next.getX()) &&
                            (cell.getY() == next.getY() - 1 || cell.getY() == next.getY() + 1 || cell.getY() == next.getY())));

                    // analysing if around cells are different from one another (except for null)
                    for (Cell other : around) {
                        if (next != other) {
                            assertNotEquals(next, other);
                        }
                    }
                }
            }
        }
    }

    @Test
    void cleanTest() {
        /* test for correct map reset
         */
        Board tester = new Board();

        // function to test
        tester.clean();

        for (int i = 0; i < tester.getLength(); i++) {
            for (Cell cell : tester.getRow(i)) {
                // checking if every cells now are free and at ground level
                assertTrue(cell.isFree() && cell.getLevel() == Level.GROUND);
            }
        }
    }

}