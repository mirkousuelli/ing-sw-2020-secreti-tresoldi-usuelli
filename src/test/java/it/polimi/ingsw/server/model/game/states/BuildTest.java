package it.polimi.ingsw.server.model.game.states;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;

import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class BuildTest {
    // TODO
    @Test
    void correctBuildTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the player picked a cell where he can build
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.BUILD);

        Block test = (Block) game.getBoard().getCell(1, 1);
        p1.initializeWorkerPosition(1, test);
        Worker worker = game.getCurrentPlayer().getWorkers().get(0);
        p1.setCurrentWorker(worker);

        // it checks if the worker is actually the chosen one
        assertEquals(worker, game.getCurrentPlayer().getCurrentWorker());

        assertTrue(game.getState() instanceof Build);

       // game.getState().gameEngine(game);
    }
}
