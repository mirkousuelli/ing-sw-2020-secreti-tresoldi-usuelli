package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class PanTest {
    /* Power:
     *   You also win if your Worker moves down two or more levels
     */

    @Test
    void testPan() throws ParserConfigurationException, SAXException {
        Game game = new Game();
        Player player1 = new Player("Pl1");
        Board board = game.getBoard();
        Deck deck = game.getDeck();
        WinConditionPower power1;

        game.addPlayer(player1);
        game.setCurrentPlayer(player1);

        deck.fetchCard(God.PAN);
        player1.setCard(deck.popRandomCard());
        power1 = (WinConditionPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        worker1Player1.setLevel(Level.MIDDLE);

        //move
        board.move(player1, empty);
        //win condition power
        assertTrue(power1.usePower(game));
    }
}
