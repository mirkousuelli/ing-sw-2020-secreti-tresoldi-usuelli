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

public class MinotaurTest {
    /* Power:
     *   Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight
     *   backwards to an unoccupied space at any level
     */

    @Test
    void testCorrectPush() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);


        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block newPos = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Minotaur
        /*power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.PUSH);*/

        //push
        assertTrue(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(newPos.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player2.getPawn(), player1.getWorkers().get(0));

        assertEquals(newPos, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player2, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testOccupiedNewPos() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);


        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block newPos = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player2.initializeWorkerPosition(2, newPos);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Minotaur
        /*power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.PUSH);*/

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
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);


        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block newPos = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Minotaur
        /*power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.PUSH);*/

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
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);


        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(3, 3);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Minotaur
        /*power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.PUSH);*/

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
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        MovePower power1;

        deck.fetchCard(God.MINOTAUR);
        player1.setCard(deck.popRandomCard());
        power1 = (MovePower) player1.getCard().getPower(0);
        //power1 = new MovePower();
        //player1.getCard().addPower(power1);


        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Minotaur
        /*power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.PUSH);*/

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
