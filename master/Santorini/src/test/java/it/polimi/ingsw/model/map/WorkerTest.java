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

import it.polimi.ingsw.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {
    private final Integer[] TEST = {2, 0};

    private final int NOT_AROUND_X = 4;
    private final int NOT_AROUND_Y = 4;

    @Test
    void movingBaseTest() {
        /* we need basically to check 3 conditions :

         * (1) CENTER BLOCK : 8 cell around
         * (2) CORNER BLOCK : 3 cell around
         * (3) SIDE BLOCK : 5 cell around
         *
         * to make this i decided to make a combination through an array of 2 elements,
         * that gives us 1 center block comb, 1 corner block comb and 2 side block com:
         *
         * - (2,2) : center block
         * - (2,0) : side block
         * - (0,2) : side block
         * - (0,0) : corner block
         */

        Board board = new Board();
        Block origin;
        Worker tester;

        for (Integer test_x : TEST) {
            for (Integer test_y : TEST) {
                // creating the starting cell
                origin = (Block) board.getCell(test_x, test_y);
                // creating the testing worker
                tester = new Worker(new Player("id"), origin);

                // initializing test
                assertSame(tester.getLocation(), origin); // right location
                assertSame(tester.getPreviousLocation(), origin); // right previous location
                assertSame(tester, origin.getPawn()); // right pawn on block

                // self move to the same place test
                tester.moveTo(origin); // self move
                assertSame(tester.getLocation(), origin); // right location
                assertSame(tester.getPreviousLocation(), origin); // right previous location
                assertSame(tester, origin.getPawn()); // right pawn on block

                // testing each direction around the current block (either center or corner or side condition)
                for (Cell around : board.getAround(tester.getLocation())) {
                    // each direction around
                    tester.moveTo(around); // move around one direction at once
                    assertSame(tester.getLocation(), around); // right current location
                    assertSame(tester.getPreviousLocation(), origin); // right previous location
                    assertSame(tester, ((Block) around).getPawn()); // right pawn on it
                    assertNull(origin.getPawn()); // right missing pawn on previous block

                    // coming back to the origin
                    tester.moveTo(origin); // move back
                    assertSame(tester.getLocation(), origin); // right current location
                    assertSame(tester.getPreviousLocation(), around); // right previous location
                    assertSame(tester, origin.getPawn()); // right pawn on it
                    assertNull(((Block) around).getPawn()); // right missing pawn on previous block
                }
            }
        }
    }

    @Test
    void movingNotAroundTest() {
        Board board = new Board();
        Block origin = new Block(TEST[0], TEST[0]);
        Worker tester = new Worker(new Player("id"), origin); // i need to initialize
        Block notAround = (Block) board.getCell(NOT_AROUND_X, NOT_AROUND_Y);

        // error move test
        tester.moveTo(notAround); // going in a wrong block not around
        assertNotSame(tester.getLocation(), notAround); // didn't go in the wrong block
        assertNotSame(tester.getPreviousLocation(), origin); // didn't leave origin
        assertNull(notAround.getPawn()); // the pawn is not on the wrong block..
        assertSame(tester, origin.getPawn()); // ..but actually is in the right block
    }

    @Test
    void movingLevelTest() {
        // (1) test of moving up of 1 level

        // (2) test of moving up for more than 1 level

        // (3) test of moving down from each level
    }

    @Test
    void movingToBusyCellTest() {
        // test of moving to a busy cell
    }

    @Test
    void buildingTest() {
        /* we need basically to check 3 conditions :

         * (1) CENTER BLOCK : 8 cell around
         * (2) CORNER BLOCK : 3 cell around
         * (3) SIDE BLOCK : 5 cell around
         *
         * to make this i decided to make a combination through an array of 2 elements,
         * that gives us 1 center block comb, 1 corner block comb and 2 side block com:
         *
         * - (2,2) : center block
         * - (2,0) : side block
         * - (0,2) : side block
         * - (0,0) : corner block
         */

        Board board = new Board();
        Block origin = new Block(TEST[0], TEST[0]);
        Worker tester = new Worker(new Player("id"), origin); // i need to initialize
        Block notAround = (Block) board.getCell(NOT_AROUND_X, NOT_AROUND_Y);

        for (Integer test_x : TEST) {
            for (Integer test_y : TEST) {
                // creating the starting cell
                origin = (Block) board.getCell(test_x, test_y);
                // creating the testing worker
                tester = new Worker(new Player("id"), origin);

                // initializing test
                assertSame(tester.getLocation(), origin); // right location
                assertSame(tester.getPreviousLocation(), origin); // right previous location
                assertSame(tester, origin.getPawn()); // right pawn on block

                // self move to the same place test
                tester.moveTo(origin); // self move
                assertSame(tester.getLocation(), origin); // right location
                assertSame(tester.getPreviousLocation(), origin); // right previous location
                assertSame(tester, origin.getPawn()); // right pawn on block

                // testing each direction around the current block (either center or corner or side condition)
                for (Cell around : board.getAround(tester.getLocation())) {
                    // each direction around
                    tester.moveTo(around); // move around one direction at once
                    assertSame(tester.getLocation(), around); // right current location
                    assertSame(tester.getPreviousLocation(), origin); // right previous location
                    assertSame(tester, ((Block) around).getPawn()); // right pawn on it
                    assertNull(origin.getPawn()); // right missing pawn on previous block

                    // coming back to the origin
                    tester.moveTo(origin); // move back
                    assertSame(tester.getLocation(), origin); // right current location
                    assertSame(tester.getPreviousLocation(), around); // right previous location
                    assertSame(tester, origin.getPawn()); // right pawn on it
                    assertNull(((Block) around).getPawn()); // right missing pawn on previous block
                }
            }
        }
    }

    @Test
    void buildingWrongCellTest() {

    }
}