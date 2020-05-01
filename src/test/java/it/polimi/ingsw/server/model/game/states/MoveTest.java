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

public class MoveTest {
    // TODO
    @Test
    void correctMoveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the player picked a cell where he can move
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        Game game = new Game();
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.MOVE);

        Block test = (Block) game.getBoard().getCell(0, 4);
        Block currentCell = (Block) game.getBoard().getCell(3, 1);
        p1.initializeWorkerPosition(1, test);
        Worker worker = game.getCurrentPlayer().getWorkers().get(0);
        p1.setCurrentWorker(worker);

        assertTrue(game.getState() instanceof Move);

        assertEquals(worker, game.getCurrentPlayer().getCurrentWorker());
        game.getBoard().getPossibleBuilds(currentCell);


        game.getState().gameEngine(game);
    }

}
