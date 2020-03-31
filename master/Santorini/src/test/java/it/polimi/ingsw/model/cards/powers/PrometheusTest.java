package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.Effect;
import it.polimi.ingsw.model.cards.powers.tags.Timing;
import it.polimi.ingsw.model.cards.powers.tags.WorkerPosition;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrometheusTest {

    @Test
    void testPrometheus() throws Exception {
        /*Power:
         *  If your Worker does not move up, it may build both before and after moving
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power1;

        player1.setCard(new Card());
        power1 = new BuildPower(player1.getCard());
        player1.getCard().setPower(power1);
        player1.getCard().setOwner(player1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Prometheus
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.BUILD);
        power1.setTiming(Timing.START_TURN);
        power1.getConstraints().setNumberOfAdditional(1);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(true);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);
        power1.setAllowedMove(MovementType.DEFAULT);
        power1.malus.setMalusType(MalusType.MOVE);
        power1.malus.setPermanent(false);
        power1.malus.setNumberOfTurns(1);
        power1.malus.addDirectionElement(MalusLevel.UP);
        power1.malus.setPersonal(true);

        //build with power
        power1.usePower(emptyBuild);
        //move
        player1.move(emptyMove);
        //build
        player1.build(emptyBuild);




        assertEquals(Level.MIDDLE, emptyBuild.getLevel());
        assertEquals(Level.BOTTOM, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(emptyMove, player1.getCurrentWorker().getLocation());
        assertEquals(player1.getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertEquals(player1.getMalusList().get(0).getDirection().get(0), MalusLevel.UP);


        //assertThrows(WrongCellException.class,
        //        () -> {player1.getCard().getPower().usePower(empty);} );
    }
}
