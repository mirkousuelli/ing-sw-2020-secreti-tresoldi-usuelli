package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TritonTest {
    /* Power:
     *   Each time your Worker moves into a perimeter space, it may immediately move again
     */

    @Test
    void testPerimCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);
        Block emptyPower2 = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Triton
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(-1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(true);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertTrue(power1.usePower(player1, emptyPower2, board.getAround(emptyPower2)));
        assertFalse(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));




        assertEquals(emptyPower2.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower2, player1.getWorkers().get(0).getLocation());
        assertEquals(emptyPower1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testNotPerimCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Triton
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(-1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(true);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertFalse(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));




        assertEquals(emptyPower1.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testOccupiedPerimCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(0, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Triton
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(-1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(true);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move with power on perim
        assertTrue(power1.usePower(player1, emptyPower1, board.getAround(emptyPower1)));
        assertFalse(power1.usePower(player1, worker2Player1, board.getAround(worker2Player1)));




        assertEquals(emptyPower1.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testDomePerimCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block dome = (Block) board.getCell(1, 0);
        Block emptyPower1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Triton
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(-1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(true);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

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
