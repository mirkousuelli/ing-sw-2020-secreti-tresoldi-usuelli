package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.God;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

class AtlasTest {
    /* Power:
     *   Your Worker may build a dome at any level
     */

    @Test
    void testCorrectDomeBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ATLAS);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build con power
        assertTrue(power1.usePower(player1, empty, board.getAround(empty)));




        assertEquals(Level.DOME, empty.getLevel());
        assertEquals(Level.GROUND, empty.getPreviousLevel());
        assertEquals(empty, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testOccupiedCellBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ATLAS);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block occupiedCell = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, occupiedCell);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build con power
        assertFalse(power1.usePower(player1, occupiedCell, board.getAround(occupiedCell)));




        assertEquals(Level.GROUND, occupiedCell.getLevel());
        assertEquals(Level.GROUND, occupiedCell.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testAlreadyDomeCellBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ATLAS);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block alreadyDomeCell = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        alreadyDomeCell.setLevel(Level.DOME);
        alreadyDomeCell.setPreviousLevel(Level.TOP);
        player1.getCurrentWorker().setPreviousBuild(alreadyDomeCell);

        //build con power
        assertFalse(power1.usePower(player1, alreadyDomeCell, board.getAround(alreadyDomeCell)));




        assertEquals(Level.DOME, alreadyDomeCell.getLevel());
        assertEquals(Level.TOP, alreadyDomeCell.getPreviousLevel());
        assertEquals(alreadyDomeCell, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testNotAdjacentCellBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ATLAS);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block notAdjacentCell = (Block) board.getCell(3, 4);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build con power
        assertFalse(power1.usePower(player1, notAdjacentCell, board.getAround(notAdjacentCell)));




        assertEquals(Level.GROUND, notAdjacentCell.getLevel());
        assertEquals(Level.GROUND, notAdjacentCell.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }
}