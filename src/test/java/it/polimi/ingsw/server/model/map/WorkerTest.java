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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkerTest {
    /*
     *
     */
    private final Integer[] TEST = {2, 0};
    private final Level[] LEVELS = Level.values();

    private final int NOT_AROUND_X = 4;
    private final int NOT_AROUND_Y = 4;

    private final int CENTER = 0;

    /* MOVE_TEST ------------------------------------------------------------------------------------------------------- */

    @Test
    void movingBaseTest() {
        /* we need basically to check 3 conditions :

         * (1) CENTER BLOCK : 8 cell around
         * (2) CORNER BLOCK : 3 cell around
         * (3) SIDE BLOCK : 5 cell around
         *
         * to make this i decided to make a combination through an array of 2 elements,
         * that gives us 1 center block comb, 1 corner block comb and 2 side block comb:
         *
         * - (2,2) : center block
         * - (2,0) : side block
         * - (0,2) : side block
         * - (0,0) : corner block
         */

        Board board = new Board();
        Block origin = (Block) board.getCell(TEST[CENTER], TEST[CENTER]);
        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        Worker tester = player.getWorkers().get(0);
        player.setCurrentWorker(tester);

        for (Integer test_x : TEST) {
            for (Integer test_y : TEST) {
                // creating the starting cell
                origin = (Block) board.getCell(test_x, test_y);
                // creating the testing worker
                tester.setLocation(origin);

                // initializing test
                assertSame(tester.getLocation(), origin); // right location
                assertSame(tester, origin.getPawn()); // right pawn on block

                // self move to the same place test
                board.move(player, origin);
                assertSame(tester.getLocation(), origin); // right location
                assertSame(tester, origin.getPawn()); // right pawn on block

                // testing each direction around the current block (either center or corner or side condition)
                for (Cell around : board.getPossibleMoves(player)) {
                    // each direction around
                    assertTrue(board.move(player, around)); // move around one direction at once
                    assertSame(tester.getLocation(), around); // right current location
                    assertSame(tester.getPreviousLocation(), origin); // right previous location
                    assertSame(tester, ((Block) around).getPawn()); // right pawn on it
                    assertNull(origin.getPawn()); // right missing pawn on previous block

                    // coming back to the origin
                    board.move(player, origin); // move back
                    assertSame(tester.getLocation(), origin); // right current location
                    assertSame(tester.getPreviousLocation(), around); // right previous location
                    assertSame(tester, origin.getPawn()); // right pawn on it
                    assertNull(((Block) around).getPawn()); // right missing pawn on previous block
                }
            }
        }
    }

    @Test
    void movingNotAroundTest()  {
        /* test that deny moving in an extra cell from around ones
         */
        Board board = new Board();
        Block origin = (Block) board.getCell(TEST[CENTER], TEST[CENTER]);;
        Block notAround = (Block) board.getCell(NOT_AROUND_X, NOT_AROUND_Y);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        Worker tester = player.getWorkers().get(0);
        player.setCurrentWorker(tester);

        // error move test
        assertFalse(board.move(player, notAround)); // going in a wrong block not around
        assertNotSame(tester.getLocation(), notAround); // didn't go in the wrong block
        assertNull(notAround.getPawn()); // the pawn is not on the wrong block..
        assertSame(tester.getLocation(), origin); // location is correct
        assertSame(tester, origin.getPawn()); // ..but actually is in the right block
    }

    @Test
    void movingLevelTest() {
        /* test that check different level moves scenarios:
         * (1) test of moving up of 1 level
         * (2) test of moving up for more than 1 level
         * (3) test of moving down from each level
         */
        Board board = new Board();
        Block origin = (Block) board.getCell(TEST[CENTER], TEST[CENTER]);
        Block next = (Block) board.getCell(origin.getX() + 1, origin.getY() + 1);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        Worker tester = player.getWorkers().get(0);
        player.setCurrentWorker(tester);

        // initializing test
        assertSame(tester.getLocation(), origin); // right location
        assertSame(tester.getPreviousLocation(), origin); // right previous location
        assertSame(tester, origin.getPawn()); // right pawn on block

        /* combination that allows me to match every case:
         * (1) test of moving up of 1 level
         * (2) test of moving up for more than 1 level
         * (3) test of moving down from each level
         */
        for (Level currLevel : LEVELS) {
            for (Level nextLevel : LEVELS) {
                if (currLevel != Level.DOME) {
                    //initializing new case to check
                    origin.setLevel(currLevel);
                    next.setLevel(nextLevel);

                    // if next cell is higher than one level compared to the origin, then..
                    if ((nextLevel.toInt() <= currLevel.toInt() + 1) && !next.isComplete()) {
                        // moving up/down to...
                        assertTrue(board.move(player, next));

                        // checking if move worked fine
                        assertSame(tester.getLocation(), next);
                        assertSame(tester.getPreviousLocation(), origin);
                        assertSame(tester, next.getPawn());
                        assertNull(origin.getPawn());

                    } else {
                        // trying to move..
                        assertFalse(board.move(player, next));
                    }

                    // resetting levels
                    origin.setLevel(Level.GROUND);
                    next.setLevel(Level.GROUND);

                    // ensuring that we are still in the origin cell
                    board.move(player, origin);
                    assertSame(tester.getLocation(), origin);
                    assertSame(tester, origin.getPawn());
                    assertNull(next.getPawn());
                }
            }
        }
    }

    @Test
    void movingToBusyCellTest() {
        /* test of moving to a busy cell
         */
        Board board = new Board();
        Block origin = (Block)board.getCell(TEST[CENTER], TEST[CENTER]);
        Block next = (Block)board.getCell(origin.getX() + 1, origin.getY() + 1);
        Worker enemy = new Worker(next);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        Worker tester = player.getWorkers().get(0);
        player.setCurrentWorker(tester);

        // trying to move to a busy cell
        board.move(player, next);
        assertNotSame(tester.getLocation(), next); // didn't go in the wrong block
        assertSame(enemy, next.getPawn()); // the pawn is not on the wrong block..
        assertSame(tester.getLocation(), origin); // location is correct
        assertSame(tester, origin.getPawn()); // ..but actually is in the right block
    }

    /* BUILD_TEST ------------------------------------------------------------------------------------------------------ */

    @Test
    void buildingBaseTest() {
        /* we need basically to check 3 conditions :

         * (1) CENTER BLOCK : 8 cell around
         * (2) CORNER BLOCK : 3 cell around
         * (3) SIDE BLOCK : 5 cell around
         *
         * to make this i decided to make a combination through an array of 2 elements,
         * that gives us 1 center block comb, 1 corner block comb and 2 side block comb:
         *
         * - (2,2) : center block
         * - (2,0) : side block
         * - (0,2) : side block
         * - (0,0) : corner block
         */

        Board board = new Board();
        Block origin = (Block) board.getCell(TEST[CENTER], TEST[CENTER]);
        Worker tester = new Worker(origin);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        player.setCurrentWorker(tester);

        // self move to the same place test
        Block finalOrigin = origin;
        board.build(player, finalOrigin); // self build
        assertNull(tester.getPreviousBuild()); // no previous build
        assertSame(tester.getLevel(), Level.GROUND); // level didn't change
        assertSame(tester, origin.getPawn()); // pawn still in the same block

        for (Integer test_x : TEST) {
            for (Integer test_y : TEST) {
                // creating the starting cell
                origin = (Block) board.getCell(test_x, test_y);
                // creating the testing worker
                tester.setLocation(origin);

                // testing each direction around the current block (either center or corner or side condition)
                for (Cell around : board.getPossibleBuilds(tester.getLocation())) {
                    // check if i start from the ground
                    assertSame(around.getLevel(), Level.GROUND);

                    // increase level
                    assertTrue(board.build(player, around));

                    // look if what i built has been saved
                    assertSame(tester.getPreviousBuild(), around);

                    // look if it has increased for real
                    assertSame(around.getLevel(), Level.BOTTOM);

                    // check if tester cell is not changed
                    assertSame(tester.getLevel(), Level.GROUND);
                    assertSame(tester.getLocation(), origin);
                    assertSame(tester, origin.getPawn());

                    // resetting
                    around.setLevel(Level.GROUND);
                }
            }
        }
    }

    @Test
    void buildingNotAroundTest() {
        /* test that deny moving in an extra cell from around ones
         */
        Board board = new Board();
        Block origin = (Block) board.getCell(TEST[CENTER], TEST[CENTER]);;
        Worker tester = new Worker(origin); // i need to initialize
        Block notAround = (Block) board.getCell(NOT_AROUND_X, NOT_AROUND_Y);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        player.setCurrentWorker(tester);

        // try to build not around
        assertFalse(board.build(player, notAround));

        // it didn't build anything
        assertNull(tester.getPreviousBuild());

        // check that location didn't change
        assertSame(tester.getLocation(), origin);
        assertSame(tester, origin.getPawn());
    }

    @Test
    void buildingLevelTest() {
        /* test that check different level moves scenarios
         */
        Board board = new Board();
        Block origin = (Block) board.getCell(TEST[CENTER], TEST[CENTER]);
        Block next = (Block) board.getCell(origin.getX() + 1, origin.getY() + 1);
        Worker tester = new Worker(origin); // i need to initialize
        Level backup;

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        player.setCurrentWorker(tester);

        // initializing test
        assertSame(tester.getLocation(), origin); // right location
        assertSame(tester.getPreviousLocation(), origin); // right previous location
        assertSame(tester, origin.getPawn()); // right pawn on block

        /* combination that allows me to match every case:
         * (1) test of moving up of 1 level
         * (2) test of moving up for more than 1 level
         * (3) test of moving down from each level
         */
        for (Level currLevel : LEVELS) {
            for (Level nextLevel : LEVELS) {
                //initializing new case to check
                origin.setLevel(currLevel);
                next.setLevel(nextLevel);

                // if next block is not complete
                if (!next.isComplete()) {
                    // build it up
                    assertTrue(board.build(player, next));

                    // look if it increased
                    assertEquals((int) next.getLevel().toInt(), next.getPreviousLevel().toInt() + 1);
                } else {
                    // save next cell level
                    backup = next.getLevel();

                    // it doesn't have to build
                    assertFalse(board.build(player, next));

                    // look if that cell remained as before
                    assertEquals(next.getLevel(), backup);
                }

                // resetting levels
                origin.setLevel(Level.GROUND);
                next.setLevel(Level.GROUND);

                // ensuring that we are still in the origin cell
                assertSame(tester.getLocation(), origin);
                assertSame(tester, origin.getPawn());
                assertNull(next.getPawn());
            }
        }
    }

    @Test
    void buildingToBusyCellTest() {
        /* test of moving to a busy cell
         */
        Board board = new Board();
        Block origin = (Block)board.getCell(TEST[CENTER], TEST[CENTER]);
        Block next = (Block)board.getCell(origin.getX() + 1, origin.getY() + 1);
        Worker enemy = new Worker(next);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        Worker tester = player.getWorkers().get(0);
        player.setCurrentWorker(tester);

        // trying to move to a busy cell
        assertFalse(board.build(player, next)); // self build
        assertNotSame(tester.getPreviousBuild(), next); // it didn't build the busy block

        // enemy check
        assertSame(enemy.getLocation(), next);
        assertSame(enemy.getLevel(), next.getPreviousLevel());
        assertSame(enemy, next.getPawn()); // the pawn is not on the wrong block..

        // worker tester check
        assertSame(tester.getLocation(), origin); // location is correct
        assertSame(tester.getLevel(), origin.getPreviousLevel());
        assertSame(tester, origin.getPawn()); // ..but actually is in the right block
    }

    @Test
    void correctFunctionsWorkerTest() {
        Board board = new Board();
        Block origin = (Block) board.getCell(0, 0);

        Player player = new Player("id");
        player.initializeWorkerPosition(1, origin);
        Worker tester = player.getWorkers().get(0);
        player.setCurrentWorker(tester);

        // check that the worker is not walkable, not complete and it is not free
        assertFalse(tester.isWalkable());
        assertFalse(tester.isFree());
        assertFalse(tester.isComplete());

        // check that if the worker's cell is a dome, the cell is complete
        tester.setLevel(Level.DOME);
        assertTrue(tester.isComplete());
        assertEquals("4",tester.getLevel().toString());

        origin.setX(2);
        origin.setY(2);
        assertEquals(2,origin.getX());
        assertEquals(2,origin.getY());

        tester.setX(1);
        tester.setY(4);
        assertEquals(1,tester.getX());
        assertEquals(4,tester.getY());

        // check if the clean works properly
        tester.clean();
        assertNull(origin.getPawn());


    }
}