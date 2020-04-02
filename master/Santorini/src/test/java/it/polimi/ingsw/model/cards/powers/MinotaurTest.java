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

public class MinotaurTest {
    /* Power:
     *   Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight
     *   backwards to an unoccupied space at any level
     */

    @Test
    void testMinotaur() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower(player1.getCard());
        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);

        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);


        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block newPos = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Minotaur
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.PUSH);

        //push
        power1.usePower(worker1Player2);




        assertEquals(newPos.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player2.getPawn(), player1.getWorkers().get(0));

        assertEquals(newPos, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player2, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
