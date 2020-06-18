package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.WinConditionPower;
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.MovementType;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.WinType;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

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
    void testMalusHashCode() {
        Malus permanentMalus = new Malus();
        Malus nonPermanentMalus = new Malus();
        Malus equalsMalus = new Malus();

        permanentMalus.setPermanent(true);
        permanentMalus.setMalusType(MalusType.BUILD);
        permanentMalus.addDirectionElement(MalusLevel.SAME);

        nonPermanentMalus.setPermanent(false);
        nonPermanentMalus.setMalusType(MalusType.MOVE);
        nonPermanentMalus.addDirectionElement(MalusLevel.DOWN);

        equalsMalus.setPermanent(false);
        equalsMalus.setMalusType(MalusType.MOVE);
        equalsMalus.addDirectionElement(MalusLevel.DOWN);




        assertNotEquals(permanentMalus, nonPermanentMalus);
        assertNotEquals(permanentMalus.hashCode(), nonPermanentMalus.hashCode());

        assertNotEquals(permanentMalus, equalsMalus);
        assertNotEquals(permanentMalus.hashCode(), nonPermanentMalus.hashCode());

        assertEquals(equalsMalus, nonPermanentMalus);
        assertEquals(equalsMalus.hashCode(), nonPermanentMalus.hashCode());
    }
}
