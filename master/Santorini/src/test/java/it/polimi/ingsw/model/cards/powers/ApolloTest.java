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

class ApolloTest {
    /* Power:
     *   Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
     */

    @Test
    void testCorrectSwap() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Apollo
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
        power1.setAllowedMove(MovementType.SWAP);

        //swap
        assertTrue(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player1.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player2.getPawn(), player1.getWorkers().get(0));

        assertEquals(worker1Player1, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player2, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testSamePlayerWorkerSwap() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Apollo
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
        power1.setAllowedMove(MovementType.SWAP);

        //swap same player worker
        assertFalse(power1.usePower(player1, worker2Player1, board.getAround(worker2Player1)));




        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertEquals(worker2Player1.getPawn(), player1.getWorkers().get(1));

        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker2Player1, player1.getWorkers().get(1).getLocation());

        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker2Player1, player1.getWorkers().get(1).getPreviousLocation());
    }

    @Test
    void testEmptyCellSwap() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Apollo
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
        power1.setAllowedMove(MovementType.SWAP);

        //swap same player worker
        assertFalse(power1.usePower(player1, empty, board.getAround(empty)));




        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));
        assertNull(empty.getPawn());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }

    @Test
    void testNotAdjacentSwap() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Apollo
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
        power1.setAllowedMove(MovementType.SWAP);

        //swap
        assertFalse(power1.usePower(player1, worker1Player2, board.getAround(worker1Player2)));




        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(worker1Player1.getPawn(), player1.getWorkers().get(0));

        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getLocation());

        assertEquals(worker1Player2, player2.getWorkers().get(0).getPreviousLocation());
        assertEquals(worker1Player1, player1.getWorkers().get(0).getPreviousLocation());
    }
}
