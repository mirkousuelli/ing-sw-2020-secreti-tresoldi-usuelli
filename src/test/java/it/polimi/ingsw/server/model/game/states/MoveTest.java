package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;

import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

    @Test
    void cannotMoveTooHighTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picks a cell that is too high, he has to move again
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block cellToMoveTo = (Block) board.getCell(0, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        worker1Player1.setLevel(Level.GROUND);
        cellToMoveTo.setLevel(Level.TOP);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);
        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(0, 1))));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(Level.GROUND, game.getCurrentPlayer().getCurrentWorker().getLevel());
        assertNull(cellToMoveTo.getPawn());
        assertEquals(worker1Player1.getPawn(), p1.getCurrentWorker());
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
    }

    @Test
    void correctMoveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell where he can move, his worker is actually moved there
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block cellToMoveTo = (Block) board.getCell(0, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToMoveTo.setLevel(Level.BOTTOM);
        worker1Player1.setLevel(Level.GROUND);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(0, 1))));
        ReturnContent returnContent = game.gameEngine();

        // check that the move is made correctly and the state is set to build
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cellToMoveTo.getPawn(), p1.getCurrentWorker());
    }

    @Test
    void cannotMoveTooFarTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picks a cell where he can't move, he has to move again
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(3, 4);
        Block cellToMoveTo = (Block) board.getCell(2, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        worker1Player1.setLevel(Level.GROUND);
        cellToMoveTo.setLevel(Level.BOTTOM);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);
        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(2, 1))));
        ReturnContent returnContent = game.gameEngine();

        // check that the worker isn't moved and the player has to pick a different cell
        assertEquals(Level.GROUND, game.getCurrentPlayer().getCurrentWorker().getLevel());
        assertNull(cellToMoveTo.getPawn());
        assertEquals(worker1Player1.getPawn(), p1.getCurrentWorker());
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
    }
    @Test
    void cannotMoveToABusyCellTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picks a cell that is already occupied, he has to move again
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(3, 4);
        Block worker2Player1 = (Block) board.getCell(3, 3);
        Block cellToMoveTo = (Block) board.getCell(3, 3);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p1.setCurrentWorker(p1.getWorkers().get(0));

        worker1Player1.setLevel(Level.BOTTOM);
        worker1Player1.setLevel(Level.GROUND);
        cellToMoveTo.setLevel(Level.BOTTOM);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);
        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(2, 1))));
        ReturnContent returnContent = game.gameEngine();

        // check that the worker isn't moved and the player has to pick a different cell
        assertEquals(Level.GROUND, game.getCurrentPlayer().getCurrentWorker().getLevel());
        assertEquals(cellToMoveTo.getPawn(), game.getCurrentPlayer().getWorkers().get(1)); // the cell is occupied by the other worker
        assertEquals(worker1Player1.getPawn(), p1.getCurrentWorker());
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
    }

}
