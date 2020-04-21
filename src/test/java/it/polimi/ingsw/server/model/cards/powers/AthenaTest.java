package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.God;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AthenaTest {
    /* Power:
     *   If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn
     */

    @Test
    void testCannotMoveUpMalus() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        Deck deck = new Deck();
        MalusPower power1;

        deck.fetchCard(God.ATHENA);
        player1.setCard(deck.popRandomCard());
        power1 = (MalusPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block emptyBuild = (Block) board.getCell(0, 1);
        Block cannotMoveUpCell = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));
        player2.initializeWorkerPosition(1, worker1Player2);
        player2.setCurrentWorker(player2.getWorkers().get(0));

        //player1
        //build
        board.build(player1, emptyBuild);
        //move
        board.move(player1, emptyBuild);
        //power
        List<Player> opponents = new ArrayList<>();
        opponents.add(player2);
        assertTrue(power1.usePower(opponents));

        //player2
        //build
        assertTrue(board.build(player2, cannotMoveUpCell));
        //move
        assertFalse(board.move(player2, cannotMoveUpCell));




        assertEquals(Level.BOTTOM, emptyBuild.getLevel());
        assertEquals(Level.GROUND, emptyBuild.getPreviousLevel());
        assertEquals(emptyBuild, player1.getCurrentWorker().getPreviousBuild());
        assertEquals(worker1Player1, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(emptyBuild, player1.getCurrentWorker().getLocation());
        assertEquals(Level.BOTTOM, cannotMoveUpCell.getLevel());
        assertEquals(Level.GROUND, cannotMoveUpCell.getPreviousLevel());
        assertEquals(cannotMoveUpCell, player2.getCurrentWorker().getPreviousBuild());
        assertEquals(worker1Player2, player2.getCurrentWorker().getLocation());
        assertEquals(MalusType.MOVE, player2.getMalusList().get(0).getMalusType());
        assertEquals(MalusLevel.UP, player2.getMalusList().get(0).getDirection().get(0));
    }
}
