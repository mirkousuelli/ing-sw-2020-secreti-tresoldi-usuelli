package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class ChronusTest {
    /* Power:
     *   You also win if your Worker moves down two or more levels
     */

    @Test
    void testChronus() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        WinConditionPower power1;

        deck.fetchCard(God.CHRONUS);
        player1.setCard(deck.popRandomCard());
        power1 = (WinConditionPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1,1);
        List<Block> towerList = new ArrayList<>();

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        for (int i = 0; i < 5; i++) {
            Block tower = (Block) board.getCell(i, 0);
            tower.setLevel(Level.DOME);
            tower.setPreviousLevel(Level.TOP);

            towerList.add(tower);
        }

        //win condition power
        //assertTrue(power1.usePower(board));
    }
}
