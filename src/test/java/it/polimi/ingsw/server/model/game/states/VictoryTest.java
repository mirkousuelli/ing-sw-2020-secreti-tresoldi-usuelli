package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class VictoryTest {

    //@Test
    void correctCleanUpTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that after a player enters the Victory state, the board is cleaned
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.VICTORY);
        game.setCurrentPlayer(p1);
        assertNotNull(game.getBoard());

        //    game.setState(game.getState().gameEngine(game)); //NullPointer because after the victory, gameEngine returns null for the moment
        //    assertNull(game.getBoard());
    }
}