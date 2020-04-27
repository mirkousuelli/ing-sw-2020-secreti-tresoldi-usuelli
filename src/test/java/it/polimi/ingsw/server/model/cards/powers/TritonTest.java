package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class TritonTest {
    /* Power:
     *   Each time your Worker moves into a perimeter space, it may immediately move again
     */

    @Test
    void testPerimCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.TRITON);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);
        Block emptyPower2 = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertTrue(power1.usePower(player1, emptyPower2, board.getAround(emptyPower2)));
        assertFalse(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));




        assertEquals(emptyPower2.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower2, player1.getWorkers().get(0).getLocation());
        assertEquals(emptyPower1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testNotPerimCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.TRITON);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertFalse(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));




        assertEquals(emptyPower1.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testOccupiedPerimCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.TRITON);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(0, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertFalse(power1.usePower(player1, worker2Player1, board.getAround(worker2Player1)));




        assertEquals(emptyPower1.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testDomePerimCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.TRITON);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block dome = (Block) board.getCell(1, 0);
        Block emptyPower1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        dome.setLevel(Level.DOME);
        dome.setPreviousLevel(Level.TOP);

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertFalse(power1.usePower(player1, dome, board.getAround(dome)));




        assertEquals(emptyPower1.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
