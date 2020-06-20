package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.ActivePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.MovementType;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.WinType;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MiscTest {

    @Test
    void testParsers() {
        assertNull(Effect.parseString("null"));
        assertNull(Timing.parseString("null"));
        assertNull(WorkerPosition.parseString("null"));
        assertNull(WorkerType.parseString("null"));
        assertNull(BlockType.parseString("null"));
        assertNull(MovementType.parseString("null"));
        assertNull(WinType.parseString("null"));
        assertNull(MalusLevel.parseString("null"));
        assertNull(MalusType.parseString("null"));

        assertEquals(WorkerPosition.TOP, WorkerPosition.parseString("TOP"));
        assertEquals(MalusLevel.SAME, MalusLevel.parseString("SAME"));
        assertEquals(MalusLevel.DEFAULT, MalusLevel.parseString("DEFAULT"));
    }

    @Test
    void testUsePowers() {
        Power power = new Power<>();
        assertFalse(power.usePower());
    }

    @Test
    void testMalus() {
        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1player1 = (Block) board.getCell(0, 0);
        Block prevLoc = (Block) board.getCell(1, 1);

        Malus permanentMalus = new Malus();
        Malus nonPermanentMalus = new Malus();
        Malus malformedMalus = new Malus();
        Malus equalsMalus = new Malus();
        Malus verifyMalus = new Malus();


        permanentMalus.setPermanent(true);
        permanentMalus.setNumberOfTurns(3);
        permanentMalus.setMalusType(MalusType.BUILD);
        permanentMalus.addDirectionElement(MalusLevel.SAME);


        nonPermanentMalus.setPermanent(false);
        nonPermanentMalus.setNumberOfTurns(1);
        nonPermanentMalus.setMalusType(MalusType.MOVE);
        nonPermanentMalus.addDirectionElement(MalusLevel.DOWN);


        malformedMalus.setPermanent(false);
        malformedMalus.setNumberOfTurns(4);


        equalsMalus.setPermanent(false);
        equalsMalus.setNumberOfTurns(1);
        equalsMalus.setMalusType(MalusType.MOVE);
        equalsMalus.addDirectionElement(MalusLevel.DOWN);


        verifyMalus.setPermanent(false);
        verifyMalus.setNumberOfTurns(1);
        verifyMalus.setMalusType(MalusType.MOVE);
        verifyMalus.addDirectionElement(MalusLevel.DEFAULT);

        player1.initializeWorkerPosition(1, worker1player1);
        player1.setCurrentWorker(player1.getWorker(1));
        player1.getWorker(1).setPreviousLocation(prevLoc);


        assertNotEquals(permanentMalus, nonPermanentMalus);
        assertNotEquals(permanentMalus.hashCode(), nonPermanentMalus.hashCode());

        assertNotEquals(permanentMalus, equalsMalus);
        assertNotEquals(permanentMalus.hashCode(), nonPermanentMalus.hashCode());

        assertEquals(equalsMalus, nonPermanentMalus);
        assertEquals(equalsMalus.hashCode(), nonPermanentMalus.hashCode());


        assertNotEquals(permanentMalus.hashCode(), malformedMalus.hashCode());
        assertNotEquals(nonPermanentMalus.hashCode(), malformedMalus.hashCode());
        assertNotEquals(equalsMalus.hashCode(), malformedMalus.hashCode());


        assertFalse(ActivePower.verifyMalus(verifyMalus, player1.getCurrentWorker()));
    }
}
