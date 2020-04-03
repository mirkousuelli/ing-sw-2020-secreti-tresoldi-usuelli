package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.powers.tags.Effect;
import it.polimi.ingsw.model.cards.powers.tags.Timing;
import it.polimi.ingsw.model.cards.powers.tags.WorkerPosition;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
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

public class PersephoneTest {
    /* Power:
     *   If possible, at least one Worker must move up this turn
     */

    //@Test
    void testMustMoveUpMalus() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        MalusPower power1;

        player1.setCard(new Card());
        power1 = new MalusPower();
        player1.getCard().setPower(power1);

        Block worker1Player2 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);

        player2.initializeWorkerPosition(1, worker1Player2);
        player2.setCurrentWorker(player2.getWorkers().get(0));

        //Persephone
        power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.MALUS);
        power1.setTiming(Timing.START_TURN);
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
        power1.malus.setPermanent(true);
        power1.malus.setNumberOfTurns(1);
        power1.malus.addDirectionElement(MalusLevel.DOWN);
        power1.malus.addDirectionElement(MalusLevel.SAME);
        power1.malus.setPersonal(false);

        emptyMove.setLevel(Level.BOTTOM);
        emptyMove.setPreviousLevel(Level.GROUND);

        //power
        List<Player> opponents = new ArrayList<>();
        opponents.add(player2);
        assertTrue(power1.usePower(opponents));
        //move up
        assertFalse(board.move(player2.getCurrentWorker(), emptyMove));




        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(player2.getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertEquals(player2.getMalusList().get(0).getDirection().get(0), MalusLevel.DOWN);
        assertEquals(player2.getMalusList().get(0).getDirection().get(1), MalusLevel.SAME);
    }
}
