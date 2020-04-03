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

public class HestiaTest {
    /* Power:
     *   Your Worker may build one additional time, but this cannot be on a perimeter space
     */

    @Test
    void testCorrectAdditionalNotPerimBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block emptyPower = (Block) board.getCell(1, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Hestia
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(true);
        power1.getConstraints().setNotSameCell(false);
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
    void testAdditionalPerimBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block emptyPower = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Hestia
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(true);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.GROUND, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
    }

    @Test
    void testAdditionalOccupiedCellBuild() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block worker1Player2 = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Hestia
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(true);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
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
    void testAdditionalNotAdjacentBuild() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(2, 2);
        Block emptyPower = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Hestia
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(true);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build
        board.build(player1.getCurrentWorker(), emptyBuild);
        //build with power
        assertFalse(power1.usePower(player1, emptyPower, board.getAround(emptyPower)));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.GROUND, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
    }
}
