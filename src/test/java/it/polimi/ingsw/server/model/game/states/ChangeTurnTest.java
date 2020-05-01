package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class ChangeTurnTest {

    @Test
    void correctChangePlayerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks after this turn the current player is changed correctly
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game1 = new Game();
        game1.addPlayer(p1);
        game1.addPlayer(p2);
        game1.addPlayer(p3);

        game1.setCurrentPlayer(p3);
        game1.setState(State.CHANGE_TURN);

        // it checks that the player and the state are correct before making the actions during the ChangeTurn state
        assertEquals(p3,game1.getCurrentPlayer());
        assertTrue(game1.getState() instanceof ChangeTurn);

        // it changes the current player
        game1.getState().gameEngine(game1);

        assertEquals(p1,game1.getCurrentPlayer()); // the current player is changed properly


        // now check if it works in a game with 2 players
        Game game2 = new Game();
        game2.addPlayer(p1);
        game2.addPlayer(p2);
        game2.setNumPlayers(2);

        game2.setCurrentPlayer(p1);
        game2.setState(State.CHANGE_TURN);

        // it checks that the player and the state are correct before making the actions during the ChangeTurn state
        assertEquals(p1,game2.getCurrentPlayer());
        assertTrue(game2.getState() instanceof ChangeTurn);

        // it changes the current player
        game2.getState().gameEngine(game2);

        assertEquals(p2,game2.getCurrentPlayer()); // the current player is changed properly
    }


    @Test
    void switchToVictoryTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the state changes to Victory if the right condition are verified
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.setNumPlayers(1);

        game.setCurrentPlayer(p1);
        game.setState(State.CHANGE_TURN);

        //it checks that the state is correct before changing the player
        assertTrue(game.getState() instanceof ChangeTurn);

        game.setState(game.getState().gameEngine(game));

        assertEquals(p1,game.getCurrentPlayer());
        assertEquals(1, game.getNumPlayers());

        assertTrue(game.getState() instanceof Victory); // the state is changed correctly
    }


    @Test
    void switchToChooseWorkerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the state changes to ChooseWorker if no one won in this turn
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setCurrentPlayer(p1);
        game.setState(State.CHANGE_TURN);

        //it checks that the state is correct before changing the player
        assertTrue(game.getState() instanceof ChangeTurn);

        // it sets the state to the new one based on the gameEngine
        game.setState(game.getState().gameEngine(game));

        assertTrue(game.getState() instanceof ChooseWorker); // the state is changed correctly
    }
}
