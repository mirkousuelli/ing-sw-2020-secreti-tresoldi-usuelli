package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class DefeatTest{

    //@Test
    void properElimination() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that after a player enters the Defeat state, he is eliminated correctly with his worker
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //game.setState(State.DEFEAT);
        game.setCurrentPlayer(p1);
        assertEquals(3,game.getNumPlayers());
        //game.setState(game.getState().gameEngine(game));

        // it checks that after one player has lost, the number of players decreases by one
        assertEquals(2,game.getNumPlayers());
        // it checks that the player is eliminated from the game
    }


    //@Test
    void switchToChangeTurnTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks if the state actually switches to ChangeTurn state
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //game.setState(State.DEFEAT);

        //assertTrue(game.getState() instanceof Defeat);
        //game.setState(game.getState().gameEngine(game));
        assertTrue(game.getState() instanceof ChangeTurn); // the state is changed correctly
    }
}
