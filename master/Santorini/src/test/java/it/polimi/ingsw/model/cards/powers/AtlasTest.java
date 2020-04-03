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

class AtlasTest {
    /* Power:
     *   Your Worker may build a dome at any level
     */

    @Test
    void testCorrectDomeBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Atlas
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DOME);

        //build con power
        assertTrue(power1.usePower(player1, empty, board.getAround(empty)));




        assertEquals(Level.DOME, empty.getLevel());
        assertEquals(Level.GROUND, empty.getPreviousLevel());
        assertEquals(empty, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testOccupiedCellBuild() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block occupiedCell = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, occupiedCell);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Atlas
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DOME);

        //build con power
        assertFalse(power1.usePower(player1, occupiedCell, board.getAround(occupiedCell)));




        assertEquals(Level.GROUND, occupiedCell.getLevel());
        assertEquals(Level.GROUND, occupiedCell.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testAlreadyDomeCellBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block alreadyDomeCell = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Atlas
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DOME);

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
    void testNotAdjacentCellBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block notAdjacentCell = (Block) board.getCell(3, 4);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Atlas
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DOME);

        //build con power
        assertFalse(power1.usePower(player1, notAdjacentCell, board.getAround(notAdjacentCell)));




        assertEquals(Level.GROUND, notAdjacentCell.getLevel());
        assertEquals(Level.GROUND, notAdjacentCell.getPreviousLevel());
        assertNull(player1.getCurrentWorker().getPreviousBuild());
    }
}