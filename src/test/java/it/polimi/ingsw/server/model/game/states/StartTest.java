package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class StartTest {

    @Test
    void correctSetUpTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks whether all the action to initialise the game are made correctly
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        assertTrue(game.getState() instanceof Start);
        assertEquals(3, game.getNumPlayers());

        game.setState(game.getState().gameEngine(game));

        // check if the Challenger and the Starter are initialised properly

    }


    @Test
    void switchToChooseWorkerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the state changes to ChooseWorker after all the actions are finished
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        // it checks the state before and after the actions to initialise the game
        assertTrue(game.getState() instanceof Start);
        game.setState(game.getState().gameEngine(game));
        assertTrue(game.getState() instanceof ChooseWorker); // the state is changed correctly
    }
}
