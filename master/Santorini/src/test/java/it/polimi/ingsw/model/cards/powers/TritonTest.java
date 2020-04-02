package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TritonTest {
    /* Power:
     *   Each time your Worker moves into a perimeter space, it may immediately move again
     */

    @Test
    void testTriton() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower(player1.getCard());
        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);

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
        assertTrue(power1.usePower(emptyPower1));
        assertTrue(power1.usePower(emptyPower2));
        assertFalse(power1.usePower(worker1Player1));




        assertEquals(emptyPower2.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower2, player1.getWorkers().get(0).getLocation());
        assertEquals(emptyPower1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
