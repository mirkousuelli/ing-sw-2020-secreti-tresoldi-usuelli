package it.polimi.ingsw.server.model.game.states;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;


public class ChooseWorkerTest {

    //@Test
    void correctEnteringToStateTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks whether if the game enters in the ChooseWorker state after everything is set up in Start state
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.getPlayerList().add(p1);
        game.getPlayerList().add(p2);
        game.getPlayerList().add(p3);

        game.setCurrentPlayer(p1);

        assertTrue(game.getState()instanceof Start);
        game.setState(new ChooseWorker(game));
        assertTrue(game.getState()instanceof ChooseWorker);
    }


    //@Test
    void switchToMoveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks if the state changes to Move after the player has chosen the worker he wants to move
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.getPlayerList().add(p1);
        game.getPlayerList().add(p2);
        game.getPlayerList().add(p3);

        game.setCurrentPlayer(p1);
        game.setState(State.CHOOSE_WORKER);

        /*
        Block w1p1 = (Block) game.getBoard().getCell(4, 1);
        Block w2p1 = (Block) game.getBoard().getCell(0, 2);
        p1.initializeWorkerPosition(1, w1p1);
        p2.initializeWorkerPosition(2, w2p1);

        assertEquals(p1.getWorkers().get(0).getLocation(), w1p1);
        assertEquals(p1.getWorkers().get(1).getLocation(), w2p1);
         */

        // it checks that the state changes correctly to Move
        assertTrue(game.getState()instanceof ChooseWorker);
   //     game.getState().gameEngine(game);
   //     assertTrue(game.getState()instanceof Move);
    }
}
