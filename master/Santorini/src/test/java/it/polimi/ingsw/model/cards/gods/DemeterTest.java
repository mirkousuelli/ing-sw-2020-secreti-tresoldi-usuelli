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
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*Power:
 *  Your Worker may build one additional time, but not on the same space
 */

class DemeterTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int emptyX = 1, emptyY = 1;

    static Player player1;
    static Board board;
    static Block worker1Player1, empty, emptySx;

    @BeforeAll
    static void init() throws Exception {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        empty = (Block) board.getCell(emptyX, emptyY);
        emptySx = (Block) board.getCell(emptyX - 1, emptyY);

        player1.initializeWorkerPosition(1, worker1Player1);

        worker1Player1.addPawn(player1.getWorker().get(0));

        player1.setCard(new Demeter());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));

        player1.build(empty);
    }

    @Test
    @Order(1)
    void testDemeter() throws Exception {
        /*@function
         * it controls if usePower functions
         */

        assertEquals(player1.getCurrentWorker().getPreviousBuild(), empty);

        player1.getCard().usePower(emptySx);

        assertEquals(Level.BOTTOM, emptySx.getLevel());
    }

    @Test
    @Order(2)
    void testSameCell() {
        /*@function
         * it controls if usePower throws an InitialSpaceException when the selected cell is the cell where the
         * current worker was before moving
         */

        assertThrows(InitialSpaceException.class,
                ()->{player1.getCard().usePower(emptySx);} );
    }
}