package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testInitializeWorkerPosition() {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(3, 2);

        assertTrue(player1.initializeWorkerPosition(1, worker1Player1));
        assertTrue(player1.initializeWorkerPosition(2, worker2Player1));

        assertEquals(player1.getWorkers().get(0).getLocation(), worker1Player1);
        assertEquals(player1.getWorkers().get(1).getLocation(), worker2Player1);
    }

    @Test
    void testNullException() {
        /*@function
         * it controls if usePower throws a NullPointerException when the selected cell is null
         */

        Player player1 = new Player("Pl1");

        assertThrows(NullPointerException.class,
                ()-> player1.initializeWorkerPosition(1, null));
    }

    @Test
    void testWrongId() {
        /*@function
         * it controls if usePower throws a WrongWorkerException when the submitted id is not 1 or 2
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(1, 1);

        assertFalse(player1.initializeWorkerPosition(3, worker1Player1));
        assertEquals(0, player1.getWorkers().size());
    }
}