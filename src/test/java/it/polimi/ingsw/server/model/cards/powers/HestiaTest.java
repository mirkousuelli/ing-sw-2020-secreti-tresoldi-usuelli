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

public class HestiaTest {
    /* Power:
     *   Your Worker may build one additional time, but this cannot be on a perimeter space
     */

    @Test
    void testCorrectAdditionalNotPerimBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.HESTIA);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block emptyPower = (Block) board.getCell(1, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build
        board.build(player1, emptyBuild);
        //build with power
        assertTrue(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyPower, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testAdditionalPerimBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.HESTIA);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block emptyPower = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build
        board.build(player1, emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.GROUND, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testAdditionalOccupiedCellBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.HESTIA);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block worker1Player2 = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build
        board.build(player1, emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.GROUND, worker1Player2.getLevel());
        assertEquals(Level.GROUND, worker1Player2.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
    }

    @Test
    void testAdditionalNotAdjacentBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.HESTIA);
        player1.setCard(deck.popRandomCard());
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block emptyPower = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build
        board.build(player1, emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.GROUND, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
    }
}
