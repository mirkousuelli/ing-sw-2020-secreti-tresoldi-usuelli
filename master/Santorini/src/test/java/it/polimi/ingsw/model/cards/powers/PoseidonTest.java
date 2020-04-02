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

public class PoseidonTest {

    @Test
    void testPoseidon() throws Exception {
        /* Power:
         *   If your unmoved Worker is on the ground level, it may build up to three times
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower(player1.getCard());
        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);

        Block worker1Player1 = (Block) board.getCell(3, 3);
        Block worker2Player1 = (Block) board.getCell(1, 1);
        Block emptyPower1 = (Block) board.getCell(0, 0);
        Block emptyPower2 = (Block) board.getCell(0, 1);
        Block emptyPower3 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Poseidon
        power1.setWorkerType(WorkerType.UNMOVED_WORKER);
        power1.setWorkerInitPos(WorkerPosition.GROUND);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.END_TURN);
        power1.getConstraints().setNumberOfAdditional(3);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);

        //build with power and an unmoved ground-level worker
        assertTrue(power1.usePower(emptyPower1));
        assertTrue(power1.usePower(emptyPower2));
        assertTrue(power1.usePower(emptyPower3));
        assertFalse(power1.usePower(emptyPower1));




        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(Level.GROUND, emptyPower1.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower1.getLevel());
        assertEquals(Level.GROUND, emptyPower2.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower2.getLevel());
        assertEquals(Level.GROUND, emptyPower3.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower3.getLevel());
        assertEquals(emptyPower3, player1.getWorkers().get(0).getPreviousBuild());
    }
}
