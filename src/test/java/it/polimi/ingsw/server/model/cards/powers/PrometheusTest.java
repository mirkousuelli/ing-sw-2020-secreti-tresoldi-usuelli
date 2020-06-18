package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class PrometheusTest {
    /* Power:
     *   If your Worker does not move up, it may build both before and after moving
     */

    @Test
    void testAdditionalBuild() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.PROMETHEUS);
        player1.setCard(deck.popCard(God.PROMETHEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power
        assertTrue(power1.usePower(player1, emptyBuild, board.getAround(emptyBuild)));
        //move
        board.move(player1, emptyMove);
        //build
        board.build(player1, emptyBuild);




        assertEquals(Level.MIDDLE, emptyBuild.getLevel());
        assertEquals(Level.BOTTOM, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(emptyMove, player1.getCurrentWorker().getLocation());
        assertEquals(MalusType.MOVE, player1.getMalusList().get(0).getMalusType());
        assertEquals(MalusLevel.UP, player1.getMalusList().get(0).getDirection().get(0));
    }

    //@Test TO-DO control malus in getPossibleMoves
    void testMoveUp() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.PROMETHEUS);
        player1.setCard(deck.popCard(God.PROMETHEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power
        assertTrue(power1.usePower(player1, emptyMove, board.getAround(emptyMove)));
        //move
        assertFalse(board.move(player1, emptyMove));




        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(worker1Player1, player1.getCurrentWorker().getLocation());
        assertEquals(MalusType.MOVE, player1.getMalusList().get(0).getMalusType());
        assertEquals(MalusLevel.UP, player1.getMalusList().get(0).getDirection().get(0));
    }

    @Test
    void testNotAdjacentCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.PROMETHEUS);
        player1.setCard(deck.popCard(God.PROMETHEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block notAdjacentCell = (Block) board.getCell(2, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power
        assertFalse(power1.usePower(player1, notAdjacentCell, board.getAround(notAdjacentCell)));




        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(worker1Player1, player1.getCurrentWorker().getLocation());
        assertEquals(0, player1.getMalusList().size());
    }

    @Test
    void testOccupiedCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.PROMETHEUS);
        player1.setCard(deck.popCard(God.PROMETHEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block occupiedCell = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, occupiedCell);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //build with power
        assertFalse(power1.usePower(player1, occupiedCell, board.getAround(occupiedCell)));




        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(worker1Player1, player1.getCurrentWorker().getLocation());
        assertEquals(0, player1.getMalusList().size());
    }

    @Test
    void testCompleteTowerCell() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        BuildPower power1;

        deck.fetchCard(God.PROMETHEUS);
        player1.setCard(deck.popCard(God.PROMETHEUS));
        power1 = (BuildPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block completeTower = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        completeTower.setPreviousLevel(Level.TOP);
        completeTower.setLevel(Level.DOME);

        //build with power
        assertFalse(power1.usePower(player1, completeTower, board.getAround(completeTower)));




        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(worker1Player1, player1.getCurrentWorker().getLocation());
        assertEquals(0, player1.getMalusList().size());
    }
}
