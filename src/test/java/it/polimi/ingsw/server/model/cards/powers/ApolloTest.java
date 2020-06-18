package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

class ApolloTest {
    /* Power:
     *   Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
     */

    @Test
    void testCorrectSwap() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if it is possible to swap a worker with an adjacent opponent worker.
         *
         * UsePower have to return true. Current and previous locations of must change accordingly.
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.APOLLO);
        player1.setCard(deck.popCard(God.APOLLO));
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //swap
        assertTrue(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player1.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player2.getPawn(), player1.getWorkers().get(0));

        assertEquals(worker1Player1, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player2, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testSamePlayerWorkerSwap() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to swap a worker with a worker of the same player.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.APOLLO);
        player1.setCard(deck.popCard(God.APOLLO));
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //swap same player worker
        assertFalse(power1.usePower(player1, worker2Player1, board.getAround(worker2Player1)));




        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));

        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker2Player1, player1.getWorkers().get(1).getLocation());

        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker2Player1, player1.getWorkers().get(1).getPreviousLocation());
    }

    @Test
    void testEmptyCellSwap() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to swap a worker with an empty cell.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.APOLLO);
        player1.setCard(deck.popCard(God.APOLLO));
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //swap empty cell
        assertFalse(power1.usePower(player1, empty, board.getAround(empty)));




        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertNull(empty.getPawn());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testNotAdjacentSwap() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to swap a worker with a non adjacent worker.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.APOLLO);
        player1.setCard(deck.popCard(God.APOLLO));
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //swap
        assertFalse(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));

        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
