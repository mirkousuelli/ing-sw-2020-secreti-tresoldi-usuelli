package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersephoneTest {
    /* Power:
     *   If possible, at least one Worker must move up this turn
     */

    @Test
    void test() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MalusPower power1;

        deck.fetchCard(God.PERSEPHONE);
        player1.setCard(deck.popRandomCard());
        power1 = (MalusPower) player1.getCard().getPower(0);

        Block worker1Player2 = (Block) board.getCell(0, 0);
        Block emptyMove = (Block) board.getCell(1, 1);
        Block tower = (Block) board.getCell(0, 1);

        player2.initializeWorkerPosition(1, worker1Player2);
        player2.setCurrentWorker(player2.getWorkers().get(0));

        tower.setLevel(Level.BOTTOM);
        tower.setPreviousLevel(Level.GROUND);

        //power
        List<Player> opponents = new ArrayList<>();
        opponents.add(player2);
        assertTrue(power1.usePower(opponents));
        //move up
        assertFalse(board.move(player2, emptyMove));




        assertEquals(worker1Player2, player2.getWorkers().get(0).getLocation());
        assertEquals(worker1Player2.getPawn(), player2.getWorkers().get(0));
        assertEquals(player2.getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertEquals(player2.getMalusList().get(0).getDirection().get(0), MalusLevel.DOWN);
        assertEquals(player2.getMalusList().get(0).getDirection().get(1), MalusLevel.SAME);
    }

    @Test
    void testMustMoveUpMalus() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MalusPower power1;

        deck.fetchCard(God.PERSEPHONE);
        player1.setCard(deck.popRandomCard());
        power1 = (MalusPower) player1.getCard().getPower(0);

        Block worker1Player2 = (Block) board.getCell(0, 0);
        Block tower = (Block) board.getCell(0, 1);

        player2.initializeWorkerPosition(1, worker1Player2);
        player2.setCurrentWorker(player2.getWorkers().get(0));

        tower.setLevel(Level.BOTTOM);
        tower.setPreviousLevel(Level.GROUND);

        //power
        List<Player> opponents = new ArrayList<>();
        opponents.add(player2);
        assertTrue(power1.usePower(opponents));
        //move up
        assertTrue(board.move(player2, tower));




        assertEquals(tower, player2.getWorkers().get(0).getLocation());
        assertEquals(tower.getPawn(), player2.getWorkers().get(0));
        assertEquals(player2.getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertEquals(player2.getMalusList().get(0).getDirection().get(0), MalusLevel.DOWN);
        assertEquals(player2.getMalusList().get(0).getDirection().get(1), MalusLevel.SAME);
    }
}
