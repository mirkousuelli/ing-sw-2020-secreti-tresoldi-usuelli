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
import it.polimi.ingsw.model.cards.gods.exceptions.NotPerimCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power:
 *  If your unmoved Worker is on the ground level, it may build up to three times
 */

class PoseidonTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int worker2Player1X = 3, worker2Player1Y = 3;
    static final int empty1X = 4, empty1Y = 3;
    static final int empty2X = 4, empty2Y = 4;

    static Player player1;
    static Board board;
    static Block worker1Player1, worker2Player1, empty1, empty2;

    @BeforeAll
    static void init() {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        worker2Player1 = (Block) board.getCell(worker2Player1X, worker2Player1Y);
        empty1 = (Block) board.getCell(empty1X, empty1Y);
        empty2 = (Block) board.getCell(empty2X, empty2Y);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);

        worker1Player1.addPawn(player1.getWorker().get(0));
        worker2Player1.addPawn(player1.getWorker().get(1));

        player1.setCard(new Poseidon());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));
    }

    @Test
    @Order(1)
    void TestPoseidon() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        assertEquals(Level.GROUND, worker1Player1.getLevel());
        assertEquals(Level.GROUND, worker2Player1.getLevel());

        player1.getCard().usePower(empty1);
        player1.getCard().usePower(empty2);
        player1.getCard().usePower(empty1);

        assertEquals(Level.MIDDLE, empty1.getLevel());
        assertEquals(Level.BOTTOM, empty2.getLevel());
    }

    // it must be done in Game
    //@Test
    //@Order(2)
    void testMoreThanThree() throws Exception {
        /*@function
         * it controls if usePower throws an Exception when a player tries to build more than three times with its
         * unmoved ground level worker
         */

        player1.getCard().usePower(empty1);
        player1.getCard().usePower(empty2);
        player1.getCard().usePower(empty1);

        assertThrows(NullPointerException.class,
                ()->{player1.getCard().usePower(empty2);} );
    }

    @Test
    @Order(2)
    void testNotOnGroundLevel() {
        /*@function
         * it controls if usePower throws an Exception when a player tries to use Poseidon's power even though its
         * unmoved worker is not on a ground level cell
         */

        player1.getWorker().get(1).moveTo(empty2);
        assertEquals(empty2, player1.getWorker().get(1).getLocation());
        assertEquals(empty2.getLevel(), player1.getWorker().get(1).getLocation().getLevel());
        assertEquals(Level.BOTTOM, player1.getWorker().get(1).getLocation().getLevel());

        assertThrows(NullPointerException.class,
                ()->{player1.getCard().usePower(empty1);} );
    }
}