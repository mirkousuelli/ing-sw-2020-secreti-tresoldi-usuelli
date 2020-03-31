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

    @Test
    void testDemeter() throws Exception {
        /*Power:
         *  Your Worker may build one additional time, but not on the same space
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower(player1.getCard());
        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);

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
        player1.build(emptyBuild);
        //build with power
        power1.usePower(emptyPower);




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(Level.BOTTOM, emptyPower.getLevel());
        assertEquals(Level.GROUND, emptyPower.getPreviousLevel());
        assertEquals(emptyPower, player1.getCurrentWorker().getPreviousBuild());


        //assertThrows(WrongCellException.class,
        //        () -> {player1.getCard().getPower().usePower(empty);} );
    }
}
