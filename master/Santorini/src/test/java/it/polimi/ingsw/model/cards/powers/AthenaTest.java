package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.WinType;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AthenaTest {
    /* Power:
     *   If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn
     */

    //@Test
    void testAthena() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        MalusPower power1;

        player1.setCard(new Card());
        power1 = new MalusPower();
        player1.getCard().setPower(power1);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));
        player2.initializeWorkerPosition(1, worker1Player2);
        player2.setCurrentWorker(player2.getWorkers().get(0));

        //Athena
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MALUS);
        power1.setTiming(Timing.END_TURN);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);
        power1.setAllowedMove(MovementType.DEFAULT);
        power1.setAllowedWin(WinType.DEFAULT);
        power1.malus.setMalusType(MalusType.MOVE);
        power1.malus.setPermanent(false);
        power1.malus.setNumberOfTurns(1);
        power1.malus.addDirectionElement(MalusLevel.UP);
        power1.malus.setPersonal(false);

        //build
        //player1.build(emptyBuild);
        //move
        //player1.move(emptyBuild);
        //power
        List<Player> opponents = new ArrayList<>();
        opponents.add(player2);
        power1.usePower(opponents);




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(emptyBuild, player1.getCurrentWorker().getLocation());
        assertEquals(player2.getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertEquals(player2.getMalusList().get(0).getDirection().get(0), MalusLevel.UP);
    }
}
