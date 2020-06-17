package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MinotaurTest {
    /* Power:
     *   Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight
     *   backwards to an unoccupied space at any level
     */

    @Test
    void testCorrectPush() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if it is possible to push backwards an adjacent opponent worker from each possible direction.
         *
         * For each direction usePower have to return true. Current and previous locations of each worker must
         * change accordingly to the direction identified by the initial position of the two workers.
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0,0);
        Block worker1Player2 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));
        player2.setCurrentWorker(player2.getWorkers().get(0));

        Block newPos;

        for (Cell c : board.getAround(board.getCell(1, 1))) {

            player1.getCurrentWorker().setLocation((Block) c);
            player2.getCurrentWorker().setLocation(worker1Player2);
            newPos = (Block) find(player1, worker1Player2, board.getAround(worker1Player2));

            //push
            assertTrue(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));


            assertEquals(newPos.getPawn(), player2.getWorkers().get(0));
            assertEquals(worker1Player2.getPawn(), player1.getWorkers().get(0));

            assertEquals(newPos, player2.getWorkers().get(0).getLocation());
            assertEquals(worker1Player2, player1.getWorkers().get(0).getLocation());

            assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
            assertEquals(c, player1.getWorkers().get(0).getPreviousLocation());
        }
    }

    private Cell find(Player currentPlayer, Cell cell, List<Cell> adjacency) {
        /*@function
         * it identifies the direction in which the opponent's worker will be forced to move
         */

        Cell currCell = currentPlayer.getCurrentWorker().getLocation();

        if (currCell.getX() < cell.getX()) {
            if (currCell.getY() < cell.getY())
                return findCell(adjacency, cell.getX() + 1, cell.getY() + 1);
            else if (currCell.getY() > cell.getY())
                return findCell(adjacency, cell.getX() + 1, cell.getY() - 1);
            else
                return findCell(adjacency, cell.getX() + 1, cell.getY());
        }
        else if (currCell.getX() > cell.getX()) {
            if (currCell.getY() < cell.getY())
                return findCell(adjacency, cell.getX() - 1, cell.getY() + 1);
            else if (currCell.getY() > cell.getY())
                return findCell(adjacency, cell.getX() - 1, cell.getY() - 1);
            else
                return findCell(adjacency, cell.getX() - 1, cell.getY());
        }
        else {
            if (currCell.getY() < cell.getY())
                return findCell(adjacency, cell.getX() , cell.getY() + 1);
            else if (currCell.getY() > cell.getY())
                return findCell(adjacency, cell.getX() , cell.getY() - 1);
        }

        return null;
    }

    private Cell findCell(List<Cell> list, int x, int y) {
        /*@function
         * it identifies the new cell of the opponent's worker
         */

        for (Cell c: list){
            if (c.getX() == x && c.getY() == y)
                return c;
        }


        return null;
    }

    @Test
    void testOccupiedNewPos() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to force an opponent worker one cell straight backwards even if it
         * is occupied.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block newPos = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player2.initializeWorkerPosition(2, newPos);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //push
        assertFalse(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(newPos.getPawn(), player2.getWorkers().get(1));

        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());
        assertEquals(newPos, player2.getWorkers().get(1).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
        assertEquals(newPos, player2.getWorkers().get(1).getPreviousLocation());
    }

    @Test
    void testDomeNewPos() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to force an opponent worker one cell straight backwards even if there
         * is a dome on that cell.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block newPos = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        newPos.setLevel(Level.DOME);
        newPos.setPreviousLevel(Level.TOP);

        //push
        assertFalse(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertNull(newPos.getPawn());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testNotAdjacentOpponent() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to force an opponent worker one cell straight backwards even if the
         * opponent worker is not adjacent.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(3, 3);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //push
        assertFalse(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));

        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testSamePlayerWorker() throws ParserConfigurationException, SAXException {
        /* @function
         * It verifies if usePower wrongly allows to force a worker of the same player.
         *
         * UsePower have to return false, current and previous locations of each worker must remain unchanged.
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //push
        assertFalse(power1.usePower(player1, worker2Player1, board.getAround(worker2Player1)));




        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));
        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));

        assertEquals(worker2Player1, player1.getWorkers().get(1).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());

        assertEquals(worker2Player1, player1.getWorkers().get(1).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
