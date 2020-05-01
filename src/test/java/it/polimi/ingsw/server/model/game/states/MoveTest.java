package it.polimi.ingsw.server.model.game.states;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;

import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {
    // TODO
    @Test
    void WrongMoveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the player picked a cell where he can move
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        Game game = new Game();

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block cellToMoveTo = (Block) board.getCell(0, 1);


        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToMoveTo.setLevel(Level.TOP);

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.MOVE);

        game.setCurrentPlayer(p1);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(0, 1))));



        ReturnContent returnContent = game.gameEngine();


        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());







        /*Block test = (Block) game.getBoard().getCell(0, 4);
        Block currentCell = (Block) game.getBoard().getCell(3, 1);
        p1.initializeWorkerPosition(1, test);
        Worker worker = game.getCurrentPlayer().getWorkers().get(0);
        p1.setCurrentWorker(worker);

        assertTrue(game.getState() instanceof Move);

        assertEquals(worker, game.getCurrentPlayer().getCurrentWorker());
        game.getBoard().getPossibleBuilds(currentCell);


        game.getState().gameEngine(game);*/
    }

    @Test
    void correctMoveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the player picked a cell where he can move
         */
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        Game game = new Game();

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block cellToMoveTo = (Block) board.getCell(0, 1);


        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToMoveTo.setLevel(Level.BOTTOM);

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.MOVE);

        game.setCurrentPlayer(p1);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(0, 1))));



        ReturnContent returnContent = game.gameEngine();


        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cellToMoveTo.getPawn(), p1.getCurrentWorker());
    }

}
