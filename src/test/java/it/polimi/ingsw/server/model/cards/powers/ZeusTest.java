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

public class ZeusTest {
    /* Power:
     *   Your Worker may build a block under itself
     */

    @Test
    void testUnderItselfBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ZEUS);
        player1.setCard(deck.popCard(God.ZEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power
        assertTrue(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));


        assertEquals(Level.BOTTOM, worker1Player1.getLevel());
        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testTopLevelTowerUnderItself() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ZEUS);
        player1.setCard(deck.popCard(God.ZEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        worker1Player1.setLevel(Level.TOP);
        worker1Player1.setPreviousLevel(Level.MIDDLE);

        //build with power
        assertFalse(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));


        assertEquals(Level.TOP, worker1Player1.getLevel());
        assertEquals(Level.MIDDLE, worker1Player1.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testNotUnderItselfCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.ZEUS);
        player1.setCard(deck.popCard(God.ZEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block notUnderItselfCell = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power
        assertFalse(power1.usePower(player1, notUnderItselfCell, board.getAround(notUnderItselfCell)));


        assertEquals(Level.GROUND, worker1Player1.getLevel());
        assertEquals(Level.GROUND, worker1Player1.getPreviousLevel());
        assertEquals(Level.GROUND, notUnderItselfCell.getLevel());
        assertEquals(Level.GROUND, notUnderItselfCell.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }
}
