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
import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class BuildTest {

    @Test
    void notPossibleBuildTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell where he can't build, he has to build again
         */
        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cellToBuildOn = (Block) board.getCell(2, 3);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToBuildOn.setLevel(Level.TOP);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(2, 3))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the impossible build is actually not allowed
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState()); // the player has to build again
        assertEquals(p1,game.getCurrentPlayer()); // the current player isn't changed since the build wasn't correctly made
        assertEquals(Level.TOP, cellToBuildOn.getLevel()); // the level of the cell isn't changed
    }


    @Test
    void correctBuildTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell where he can build, the
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(2, 2);
        Block cellToBuildOn = (Block) board.getCell(2, 3);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToBuildOn.setLevel(Level.MIDDLE);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);
        assertEquals(Level.MIDDLE, cellToBuildOn.getLevel()); // the level of the cell before the build is correct


        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(2, 3))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the build is made successfully
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.TOP, cellToBuildOn.getLevel()); // the level of the chosen cell is increased by one after the build

        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
        assertEquals(State.CHOOSE_WORKER, returnContent.getState()); // the state is now chooseWorker (for the new current player)
    }

    @Test
    void cannotBuildUnderTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell under his current worker, he has to choose a different one
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cellToBuildOn = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToBuildOn.setLevel(Level.TOP);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(1, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the player cannot build under itself (only zeus can)
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(p1,game.getCurrentPlayer());
        assertEquals(Level.TOP, cellToBuildOn.getLevel());
    }

    @Test
    void cannotBuildOnDomeTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell where there's a dome, he has to choose a different one
         */
        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cellToBuildOn = (Block) board.getCell(2, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToBuildOn.setLevel(Level.DOME);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(2, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that it's not possible to build on a dome
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState()); // the player has to build again
        assertEquals(p1,game.getCurrentPlayer()); // the current player isn't changed since the build wasn't correctly made
        assertEquals(Level.DOME, cellToBuildOn.getLevel()); // the level of the cell isn't changed
    }
}
