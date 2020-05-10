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



/*
___________________________________________________________________________________________________________________________________
    TESTS ON GODS POWER
*/



    // ATLAS: Your Worker may build a dome at any level.
    @Test
    void buildingWithAtlas() throws ParserConfigurationException, SAXException {

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

        cellToBuildOn.setLevel(Level.BOTTOM);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.ATLAS);
        assertEquals(Level.BOTTOM, cellToBuildOn.getLevel()); // the level of the cell before the build is correct


        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(2, 3))));
        ReturnContent returnContent = game.gameEngine();

        //it checks that the build is made successfully
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.DOME, cellToBuildOn.getLevel()); // the level of the chosen cell is increased by one after the build

        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
        assertEquals(State.CHOOSE_WORKER, returnContent.getState()); // the state is now chooseWorker (for the new current player)
    }


    // Normal use power works properly
    // DEMETER: Your Worker may build one additional time, but not on the same space.
    @Test
    void buildingWithDemeter() throws ParserConfigurationException, SAXException {
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
        Block worker1Player1 = (Block) board.getCell(2, 3);
        Block cell1 = (Block) board.getCell(2, 2);
        Block cell2 = (Block) board.getCell(3, 3);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        cell1.setLevel(Level.MIDDLE);
        cell2.setLevel(Level.GROUND);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.DEMETER);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(2, 2))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the build is made successfully
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.TOP, cell1.getLevel());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());

        game.setState(State.ADDITIONAL_POWER);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.ASK_ADDITIONAL_POWER, new ReducedDemandCell(-1, -1))));
        ReturnContent rc = game.gameEngine();

        // it checks that if the player picked a different cell, the block is built and the turn is changed
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(Level.GROUND, cell2.getLevel());
        assertEquals(State.CHOOSE_WORKER, rc.getState());
        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
    }


    // Normal use power works properly
    // HEPHAESTUS: Your Worker may build one additional block (not dome) on top of your first block.
    @Test
    void buildingWithHephaestus() throws ParserConfigurationException, SAXException {
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
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block cellToBuildOn = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToBuildOn.setLevel(Level.BOTTOM);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.HEPHAESTUS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(1, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the player
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.MIDDLE, cellToBuildOn.getLevel());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(1, 1))));
        ReturnContent rc = game.gameEngine();
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(Level.TOP, cellToBuildOn.getLevel());
        assertEquals(State.CHOOSE_WORKER, rc.getState());
        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
    }

    @Test
    void wrongBuildingWithHephaestus() throws ParserConfigurationException, SAXException {
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
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block cellToBuildOn = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToBuildOn.setLevel(Level.MIDDLE);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.HEPHAESTUS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(1, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the player
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.TOP, cellToBuildOn.getLevel());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(1, 1))));
        ReturnContent rc = game.gameEngine();
        assertEquals(AnswerType.ERROR, rc.getAnswerType());
        assertEquals(Level.TOP, cellToBuildOn.getLevel());
        assertEquals(State.BUILD, rc.getState());
        assertEquals(p1,game.getCurrentPlayer()); // the current player is still p1
    }

    @Test
    void buildingWithPrometheus() throws ParserConfigurationException, SAXException {
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
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block cellToBuildOn1 = (Block) board.getCell(1, 1);
        Block cellToBuildOn2 = (Block) board.getCell(0, 1);
        Block cellToMoveOn = (Block) board.getCell(0, 0);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.PROMETHEUS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(1, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the player
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToBuildOn1.getLevel());
        assertEquals(State.MOVE, returnContent.getState());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(0, 0))));

        returnContent = game.gameEngine();

        //it checks that the player
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.GROUND, cellToMoveOn.getLevel());
        assertEquals(worker1Player1, p1.getCurrentWorker().getPreviousLocation());
        assertEquals(cellToMoveOn, p1.getCurrentWorker().getLocation());
        assertEquals(State.BUILD, returnContent.getState());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(0, 1))));

        game.setState(State.BUILD);
        ReturnContent rc = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(Level.BOTTOM, cellToBuildOn2.getLevel());
        assertEquals(State.CHOOSE_WORKER, rc.getState());
        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
    }

    @Test
    void wrongBuildingWithPrometheus() throws ParserConfigurationException, SAXException {
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
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block cellToDoActions = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.PROMETHEUS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(1, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the player
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToDoActions.getLevel());
        assertEquals(State.MOVE, returnContent.getState());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(1, 1))));

        returnContent = game.gameEngine();

        //it checks that the player
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToDoActions.getLevel());
        assertEquals(worker1Player1, p1.getCurrentWorker().getPreviousLocation());
        assertEquals(worker1Player1, p1.getCurrentWorker().getLocation());
        assertEquals(State.MOVE, returnContent.getState());
    }


    // Hestia: Can build a second time but not on a perimeter cell
  //  @Test
    void buildingWithHestia() throws ParserConfigurationException, SAXException {
        /*@function
         *
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
        Block cell1 = (Block) board.getCell(1, 2);
        Block cell2 = (Block) board.getCell(2, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        cell1.setLevel(Level.MIDDLE);
        cell2.setLevel(Level.GROUND);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.BUILD, new ReducedDemandCell(1, 2))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the build is made successfully
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.TOP, cell1.getLevel());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(-1, -1))));
        ReturnContent rc = game.gameEngine();

        // it checks that if the player picked a different cell, the block is built and the turn is changed
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(Level.BOTTOM, cell2.getLevel());
        assertEquals(State.CHOOSE_WORKER, rc.getState());
        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
    }


    // ZEUS: Your Worker may build a block under itself.
    @Test
    void buildingUnderWithZeusTest() throws ParserConfigurationException, SAXException {
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
        Block w1p1 = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, w1p1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        w1p1.setLevel(Level.BOTTOM);

        game.setState(State.BUILD);
        game.setCurrentPlayer(p1);
        game.assignCard(God.ZEUS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(1, 1))));

        ReturnContent returnContent = game.gameEngine();

        //it checks that the player build under itself since he has zeus
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
        assertEquals(p2,game.getCurrentPlayer()); // the current player is now the next one
        assertEquals(Level.MIDDLE, w1p1.getLevel());
    }
}
