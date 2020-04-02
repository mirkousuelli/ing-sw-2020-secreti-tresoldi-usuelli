package it.polimi.ingsw.model;

import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    static Player player1;
    static Board board;
    static Block worker1Player1, worker2Player1;

    @BeforeAll
    static void init() {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        board = new Board();
        worker1Player1 = (Block) board.getCell(1, 1);
        worker2Player1 = (Block) board.getCell(3, 2);
    }

    @Test
    void testInitializeWorkerPosition() {
        /*@function
         * it controls if usePower functions in the right way
         */

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);

        assertEquals(player1.getWorkers().get(0).getLocation(), worker1Player1);
        assertEquals(player1.getWorkers().get(1).getLocation(), worker2Player1);
    }

    @Test
    void testNullException() {
        /*@function
         * it controls if usePower throws a NullPointerException when the selected cell is null
         */

        assertThrows(NullPointerException.class,
                ()->{player1.initializeWorkerPosition(1, null);} );
    }

    @Test
    void testWrongId() {
        /*@function
         * it controls if usePower throws a WrongWorkerException when the submitted id is not 1 or 2
         */

        /*assertThrows(WrongWorkerException.class,
                ()->{player1.initializeWorkerPosition(6, worker1Player1);} );*/
    }
}