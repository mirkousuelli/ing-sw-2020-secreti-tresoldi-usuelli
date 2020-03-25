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
import it.polimi.ingsw.model.cards.gods.exceptions.EmptyCellException;
import it.polimi.ingsw.model.cards.gods.exceptions.WrongWorkerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*Power:
 *  Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
 */

class ApolloTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int worker2Player1X = 1, worker2Player1Y = 1;
    static final int worker1Player2X = 4, worker1Player2Y = 3;
    static final int emptyX = 4, emptyY = 4;

    static Player player1, player2;
    static Board board;
    static Block worker1Player1, worker1Player2, worker2Player1, empty;

    @BeforeAll
    static void init() {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        player2 = new Player("Pl2");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        worker2Player1 = (Block) board.getCell(worker2Player1X, worker2Player1Y);
        worker1Player2 = (Block) board.getCell(worker1Player2X, worker1Player2Y);
        empty = (Block) board.getCell(emptyX, emptyY);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        worker1Player1.addPawn(player1.getWorker().get(0));
        worker2Player1.addPawn(player1.getWorker().get(1));
        worker1Player2.addPawn(player2.getWorker().get(0));

        player1.setCard(new Apollo());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));
    }

    @Test
    void testApollo() throws Exception{
        /*@function
         * it controls if usePower functions in the right way
         */

        player1.getCard().usePower(worker1Player2);

        // it controls whether the swap happened or not
        assertEquals(player1.getCurrentWorker().getLocation(), worker1Player2);
        assertEquals(player2.getWorker().get(0).getLocation(), worker1Player1);

        // it controls whether the method modified happened or not
        assertEquals(player1.getCurrentWorker().getPreviousLocation(), worker1Player1);
        assertEquals(player2.getWorker().get(0).getPreviousLocation(), worker1Player2);
    }

    @Test
    void testWrongWorker() {
        /*@function
         * it controls if usePower throws a WrongWorkerException when cell contains a worker of the current player
         */

        assertThrows(WrongWorkerException.class,
                ()->{player1.getCard().usePower(worker2Player1);} );
    }

    @Test
    void testEmptyCell() {
        /*@function
         * it controls if usePower throws an EmptyCellException when cell does not contains a worker
         */

        assertThrows(EmptyCellException.class,
                ()->{player1.getCard().usePower(empty);} );
    }
}