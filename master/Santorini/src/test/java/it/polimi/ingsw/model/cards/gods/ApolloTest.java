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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.cards.EmptyCellException;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

/*Power:
 *  Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
 */

class ApolloTest {

    @Test
    void testApollo() throws Exception{
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);
        Block worker1Player2 = (Block) board.getCell(4, 3);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        player1.setCard(new Apollo());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        player1.getCard().usePower(worker1Player2);

        // it controls whether the swap happened or not
        assertEquals(player1.getCurrentWorker().getLocation(), worker1Player2);
        assertEquals(player2.getWorkers().get(0).getLocation(), worker1Player1);

        // it controls whether the method modified happened or not
        assertEquals(player1.getCurrentWorker().getPreviousLocation(), worker1Player1);
        assertEquals(player2.getWorkers().get(0).getPreviousLocation(), worker1Player2);
    }

    @Test
    void testWrongWorker() throws Exception {
        /*@function
         * it controls if usePower throws a WrongWorkerException when the selected cell contains a worker
         * of the current player
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);

        player1.setCard(new Apollo());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(WrongWorkerException.class,
                ()->{player1.getCard().usePower(worker2Player1);} );
    }

    @Test
    void testEmptyCell() throws Exception {
        /*@function
         * it controls if usePower throws an EmptyCellException when cell does not contains a worker
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(4, 4);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Apollo());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(EmptyCellException.class,
                ()->{player1.getCard().usePower(empty);} );
    }
}