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

public class ArthemisTest {

    @Test
    void testArthemis() throws Exception {
        /*Power:
         *  Your Worker may move one additional time, but not back to its initial space
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power1;

        player1.setCard(new Card());
        power1 = new MovePower(player1.getCard());
        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block emptyPower = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Arthemis
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MOVE);
        power1.setTiming(Timing.ADDITIONAL);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedMove(MovementType.DEFAULT);

        //move
        player1.move(emptyMove);
        //move with power
        power1.usePower(emptyPower);




        assertEquals(emptyPower.getPawn(), player1.getWorkers().get(0));
        assertEquals(emptyPower, player1.getWorkers().get(0).getLocation());
        assertEquals(emptyMove, player1.getWorkers().get(0).getPreviousLocation());


        /*game.setCellToUse(empty);
        game.getState().gameEngine();
        assertThrows(WrongCellException.class,
                () -> {player1.getCard().getPower().usePower(worker1Player1);} );*/
    }
}
