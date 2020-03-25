/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.gods.exceptions.InitialSpaceException;
import it.polimi.ingsw.model.cards.gods.exceptions.OccupiedCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*Power:
 *  Your Worker may move one additional time, but not back to its initial space
 */

class ArthemisTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int worker1Player2X = 0, worker1Player2Y = 2;
    static final int emptyX = 1, emptyY = 1;

    static Player player1, player2;
    static Board board;
    static Block worker1Player1, worker1Player2, empty, emptySx;

    @BeforeAll
    static void init() throws Exception {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        player2 = new Player("Pl2");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        worker1Player2 = (Block) board.getCell(worker1Player2X, worker1Player2Y);
        empty = (Block) board.getCell(emptyX, emptyY);
        emptySx = (Block) board.getCell(emptyX + 1, emptyY);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        worker1Player1.addPawn(player1.getWorker().get(0));
        worker1Player2.addPawn(player2.getWorker().get(0));

        player1.setCard(new Arthemis());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));
        player1.move(empty);
    }

    @Test
    @Order(1)
    void testArthemis() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        player1.getCard().usePower(emptySx);

        assertEquals(empty, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(emptySx, player1.getCurrentWorker().getLocation());
    }

    @Test
    @Order(2)
    void testInitialCell() {
        /*@function
         * it controls if usePower throws an InitialSpaceException when the selected cell is the cell where the
         * current worker was before moving
         */

        assertThrows(InitialSpaceException.class,
                ()->{player1.getCard().usePower(empty);} );
    }

    @Test
    @Order(3)
    void testOccupiedCell() {
        /*@function
         * it controls if usePower throws an OccupiedCellException when the selected cell is occupied by an other worker
         */

        assertThrows(OccupiedCellException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );
    }

}