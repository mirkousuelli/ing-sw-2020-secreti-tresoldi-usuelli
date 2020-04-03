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

public class DemeterTest {
    /* Power:
     *   Your Worker may build one additional time, but not on the same space
     */

    @Test
    void testCorrectDifferentCellBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyBuild = (Block) board.getCell(1, 1);
        Block emptyPower = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Demeter
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertTrue(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyPower, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testSameCellBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyBuild = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Demeter
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, emptyBuild, board.getAround(emptyBuild)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testOccupiedCellBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyBuild = (Block) board.getCell(1, 1);
        Block occupiedCell = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, occupiedCell);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Demeter
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, occupiedCell, board.getAround(occupiedCell)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.GROUND, occupiedCell.getLevel());
        assertEquals(Level.GROUND, occupiedCell.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
        assertEquals(occupiedCell.getPawn(), player1.getWorkers().get(1));
        assertEquals(occupiedCell, player1.getWorkers().get(1).getLocation());
    }

    @Test
    void testCompleteTowerBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyBuild = (Block) board.getCell(1, 1);
        Block completeTower = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Demeter
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        completeTower.setPreviousLevel(Level.TOP);
        completeTower.setLevel(Level.DOME);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, completeTower, board.getAround(completeTower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
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
        Block emptyBuild = (Block) board.getCell(1, 1);
        Block notAdjacent = (Block) board.getCell(3, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Demeter
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, notAdjacent, board.getAround(notAdjacent)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
    }
}
