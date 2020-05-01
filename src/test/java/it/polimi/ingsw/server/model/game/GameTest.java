package it.polimi.ingsw.server.model.game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.states.*;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    @Test
    void creationGameTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it creates a new game and checks that all the objects are created properly
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game1 = new Game();
        game1.addPlayer(p1);
        game1.addPlayer(p2);
        game1.addPlayer(p3);

        assertEquals(0, game1.getPlayerList().indexOf(p1));
        assertEquals(3, game1.getNumPlayers());
        assertEquals("Mirko",game1.getPlayer(1).getNickName());

        // it checks that the current player is the first one and then changes
        assertEquals(p1, game1.getCurrentPlayer());
        game1.setCurrentPlayer(p2);
        assertEquals(p2, game1.getCurrentPlayer());

        // it checks if it works for a 2 players game as well
        Game game2 = new Game();
        game2.addPlayer(p1);
        game2.addPlayer("Mirko");

        assertEquals(0, game2.getPlayerList().indexOf(p1));
        assertEquals(2, game2.getNumPlayers());
        assertEquals(p1 ,game2.getPlayerList().get(0));
    }


    @Test
    void enteringStartStateTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that when a game is created the state is set to start
         */
        Game game = new Game();

        assertTrue(game.getState()instanceof Start);
    }
}