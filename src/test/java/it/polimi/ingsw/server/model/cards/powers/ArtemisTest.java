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

public class ArtemisTest {
    /* Power:
     *   Your Worker may move one additional time, but not back to its initial space
     */

    @Test
    void testAdditionalDifferentCellMove() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if it is possible to move one additional time.
         *
         * UsePower have to return true. Current and previous locations of must change accordingly.
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.ARTEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block emptyPower = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //move
        board.move(player1, emptyMove);
        //move with power
        assertTrue(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(emptyPower.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower, player1.getWorkers().get(0).getLocation());
        assertEquals(emptyMove, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testAdditionalSameCellMove() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.ARTEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //move
        board.move(player1, emptyMove);
        //move with power
        assertFalse(power1.usePower(player1, emptyMove, board.getAround(emptyMove)));




        assertEquals(emptyMove.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyMove, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testAdditionalMoreThanOneLevelMove() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.ARTEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block moreThanOneLevelMove = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        moreThanOneLevelMove.setLevel(Level.MIDDLE);
        moreThanOneLevelMove.setPreviousLevel(Level.BOTTOM);

        //move
        board.move(player1, emptyMove);
        //move with power
        assertFalse(power1.usePower(player1, moreThanOneLevelMove, board.getAround(moreThanOneLevelMove)));




        assertEquals(emptyMove.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyMove, player1.getWorkers().get(0).getLocation());
        assertNull(moreThanOneLevelMove.getPawn());
        assertEquals(Level.MIDDLE, moreThanOneLevelMove.getLevel());
        assertEquals(Level.BOTTOM, moreThanOneLevelMove.getPreviousLevel());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testAdditionalDomeMove() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.ARTEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block dome = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        dome.setLevel(Level.DOME);
        dome.setPreviousLevel(Level.TOP);

        //move
        board.move(player1, emptyMove);

        emptyMove.setLevel(Level.TOP);
        emptyMove.setPreviousLevel(Level.MIDDLE);

        //move with power
        assertFalse(power1.usePower(player1, dome, board.getAround(dome)));




        assertEquals(emptyMove.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyMove, player1.getWorkers().get(0).getLocation());
        assertNull(dome.getPawn());
        assertEquals(Level.DOME, dome.getLevel());
        assertEquals(Level.TOP, dome.getPreviousLevel());
        assertEquals(Level.TOP, emptyMove.getLevel());
        assertEquals(Level.MIDDLE, emptyMove.getPreviousLevel());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testOccupiedCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.ARTEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //move
        board.move(player1, emptyMove);

        //move with power
        assertFalse(power1.usePower(player1, worker2Player1, board.getAround(worker2Player1)));




        assertEquals(emptyMove.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyMove, player1.getWorkers().get(0).getLocation());
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));
        assertEquals(worker2Player1, player1.getWorkers().get(1).getLocation());
        assertEquals(Level.GROUND, worker2Player1.getLevel());
        assertEquals(Level.GROUND, worker2Player1.getPreviousLevel());
        assertEquals(Level.GROUND, emptyMove.getLevel());
        assertEquals(Level.GROUND, emptyMove.getPreviousLevel());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
