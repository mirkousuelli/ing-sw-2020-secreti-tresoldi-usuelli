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

class BlockTest {
    private final int TEST_X = 0;
    private final int TEST_Y = 0;

    @Test
    void levelStressTest() {
        /* test that forces building up and down exceeding level limits
         */
        Block block = new Block(TEST_X, TEST_Y);

        // i initialize a reverse array to be populated during the first iteration
        // (i think i cannot import libraries which includes reverse function
        // applied to an array w/o asking to a tutor)
        Level[] reverse = new Level[Level.values().length];
        int i = Level.values().length - 1;

        // 1st iteration for building up correctly
        for (Level level : Level.values()) {
            assertSame(block.getLevel(), level);
            block.setLevel(block.getLevel().buildUp());

            // preparing 2nd array reversed
            reverse[i--] = level;
        }

        // limit case of superior border repeated twice
        assertSame(block.getLevel().buildUp(), Level.DOME);

        // 2nd iteration for building down correctly
        for (Level level : reverse) {
            assertSame(block.getLevel(), level);
            block.setLevel(block.getLevel().buildDown());
        }

        // limit case of inferior border repeated twice
        assertSame(block.getLevel().buildDown(), Level.GROUND);
    }

    @Test
    void cleanTest() {
        /* test which check atomic cleaning
         */
        Block block = new Block(TEST_X, TEST_Y);

        // check current level reset
        assertEquals(Level.GROUND, block.getLevel());
        // check previous level reset
        assertEquals(Level.GROUND, block.getPreviousLevel());
        //check that cell i free
        assertTrue(block.isFree());
    }

    @Test
    void pawnPresenceTest() {
        /* test for correct assumption of add/remove a pawn on the block
         */
        Block block = new Block(TEST_X, TEST_Y);

        // check if it is free and walkable
        assertTrue(block.isWalkable());
        // adding a pawn on it
        block.addPawn(new Worker(block));
        // checking that now it is not possible anymore
        assertFalse(block.isWalkable());
        // removing previous pawn
        block.removePawn();
        // checking that now it is possible to walk on it because it is free
        assertTrue(block.isWalkable());
    }

}