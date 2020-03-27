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
import it.polimi.ingsw.model.exceptions.map.NotValidCellException;
import it.polimi.ingsw.model.exceptions.map.NotValidLevelException;
import it.polimi.ingsw.model.exceptions.map.PawnPositioningException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BlockTest {
    private final int TEST_X = 0;
    private final int TEST_Y = 0;

    @Test
    void levelStressTest() throws NotValidLevelException, NotValidCellException {
        /* test that forces building up and down exceeding level limits
         */
        Block block = new Block(TEST_X, TEST_Y, new Board());

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
    void cleanTest() throws NotValidCellException {
        /* test which check atomic cleaning
         */
        Block block = new Block(TEST_X, TEST_Y, new Board());

        // check current level reset
        assertEquals(Level.GROUND, block.getLevel());
        // check previous level reset
        assertEquals(Level.GROUND, block.getPreviousLevel());
        //check that cell i free
        assertTrue(block.isFree());
    }

    @Test
    void pawnPresenceTest() throws PawnPositioningException, NotValidCellException {
        /* test for correct assumption of add/remove a pawn on the block
         */
        Block block = new Block(TEST_X, TEST_Y, new Board());

        // check if it is free and walkable
        assertTrue(block.isWalkable());
        // adding a pawn on it
        block.addPawn(new Worker(new Player("id"), block));
        // checking that now it is not possible anymore
        assertFalse(block.isWalkable());
        // removing previous pawn
        block.removePawn();
        // checking that now it is possible to walk on it because it is free
        assertTrue(block.isWalkable());
    }

}