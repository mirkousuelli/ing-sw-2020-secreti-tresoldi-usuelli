package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZeusTest {
    /* Power:
     *   Your Worker may build a block under itself
     */

    @Test
    void testUnderItselfBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Zeus
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(true);
        power1.setAllowedBlock(BlockType.NOT_DOME);

        //build with power
        assertTrue(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));




        assertEquals(Level.BOTTOM, worker1Player1.getLevel());
        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testTopLevelTowerUnderItself() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Zeus
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(true);
        power1.setAllowedBlock(BlockType.NOT_DOME);

        worker1Player1.setLevel(Level.TOP);
        worker1Player1.setPreviousLevel(Level.MIDDLE);

        //build with power
        assertFalse(power1.usePower(player1, worker1Player1, board.getAround(worker1Player1)));




        assertEquals(Level.TOP, worker1Player1.getLevel());
        assertEquals(Level.MIDDLE, worker1Player1.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testNotUnderItselfCell() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block notUnderItselfCell = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Zeus
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(true);
        power1.setAllowedBlock(BlockType.NOT_DOME);

        //build with power
        assertFalse(power1.usePower(player1, notUnderItselfCell, board.getAround(notUnderItselfCell)));




        assertEquals(Level.GROUND, worker1Player1.getLevel());
        assertEquals(Level.GROUND, worker1Player1.getPreviousLevel());
        assertEquals(Level.GROUND, notUnderItselfCell.getLevel());
        assertEquals(Level.GROUND, notUnderItselfCell.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }
}
