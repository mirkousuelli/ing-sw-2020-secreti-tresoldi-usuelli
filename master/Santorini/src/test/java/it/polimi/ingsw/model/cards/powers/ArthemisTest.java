package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class ArthemisTest {
    /* Power:
     *   Your Worker may move one additional time, but not back to its initial space
     */

    @Test
    void testAdditionalDifferentCellMove() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.ARTHEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block emptyPower = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Arthemis
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move
        //player1.move(emptyMove);
        player1.getCurrentWorker().setPreviousLocation(worker1Player1);
        player1.getCurrentWorker().setLocation(emptyMove);
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

        deck.fetchCard(God.ARTHEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Arthemis
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move
        //player1.move(emptyMove);
        player1.getCurrentWorker().setPreviousLocation(worker1Player1);
        player1.getCurrentWorker().setLocation(emptyMove);
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

        deck.fetchCard(God.ARTHEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block moreThanOneLevelMove = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Arthemis
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        moreThanOneLevelMove.setLevel(Level.MIDDLE);
        moreThanOneLevelMove.setPreviousLevel(Level.BOTTOM);

        //move
        //player1.move(emptyMove);
        player1.getCurrentWorker().setPreviousLocation(worker1Player1);
        player1.getCurrentWorker().setLocation(emptyMove);
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

        deck.fetchCard(God.ARTHEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block dome = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Arthemis
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        dome.setLevel(Level.DOME);
        dome.setPreviousLevel(Level.TOP);

        //move
        //player1.move(emptyMove);
        player1.getCurrentWorker().setPreviousLocation(worker1Player1);
        player1.getCurrentWorker().setLocation(emptyMove);

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

        deck.fetchCard(God.ARTHEMIS);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Arthemis
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move
        //player1.move(emptyMove);
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
