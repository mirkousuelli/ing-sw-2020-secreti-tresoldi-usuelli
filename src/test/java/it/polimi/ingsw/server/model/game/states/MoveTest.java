package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
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
    void playerWinsTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player moved to a third level, he wins
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

        cellToMoveTo.setLevel(Level.TOP);
        worker1Player1.setLevel(Level.MIDDLE);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(0, 1))));
        ReturnContent returnContent = game.gameEngine();

        // check that the player moved to a third level and the state is set to victory
        assertEquals(AnswerType.VICTORY, returnContent.getAnswerType());
        assertEquals(State.VICTORY, returnContent.getState());
        assertEquals(cellToMoveTo.getPawn(), p1.getCurrentWorker());
        assertEquals(Level.TOP, cellToMoveTo.getLevel());
    }


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
         * it checks that if the player picks a cell that is already occupied by another worker, he has to move again
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block w1p1 = (Block) board.getCell(3, 4);
        Block w1p2 = (Block) board.getCell(3, 3);

        p1.initializeWorkerPosition(1, w1p1);
        p2.initializeWorkerPosition(2, w1p2);

        p1.setCurrentWorker(p1.getWorkers().get(0));

        w1p1.setLevel(Level.BOTTOM);
        w1p2.setLevel(Level.GROUND);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.ZEUS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(3, 3))));
        ReturnContent returnContent = game.gameEngine();

        // check that the worker isn't moved and the player has to pick a different cell
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());

        assertEquals(Level.BOTTOM, game.getCurrentPlayer().getCurrentWorker().getLevel());
        assertEquals(w1p2.getPawn(), p2.getWorkers().get(0)); // the cell is occupied by the other worker
        assertEquals(w1p1.getPawn(), p1.getWorkers().get(0)); // the worker stays in the previous cell
    }




/*
___________________________________________________________________________________________________________________________________
    TESTS ON GODS POWER
*/



    //
    // ARTEMIS: Your Worker may move one additional time, but not back to its initial space.
    @Test
    void movingWithArtemisTest() throws ParserConfigurationException, SAXException {
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
        Block cell1 = (Block) board.getCell(1, 1);
        Block cell2 = (Block) board.getCell(2, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cell1.setLevel(Level.BOTTOM);
        cell2.setLevel(Level.BOTTOM);
        worker1Player1.setLevel(Level.GROUND);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.MOVE, new ReducedDemandCell(1, 1))));
        ReturnContent returnContent = game.gameEngine();

        // check that the state is still move since Artemis power allows the player to move a second time
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cell1.getPawn(), p1.getCurrentWorker());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(2, 2))));
        ReturnContent rc = game.gameEngine();

        // check that the move is made correctly and the state is set to build
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(State.BUILD, rc.getState());
        assertNull(cell1.getPawn());
        assertEquals(cell2.getPawn(), p1.getCurrentWorker());

    }

    
    // APOLLO: Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.
    @Test
    void movingWithApolloTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Apollo as God, he can move to an occupied cell and the workers ae swapped
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block w1p1 = (Block) board.getCell(3, 4);
        Block w1p2 = (Block) board.getCell(3, 3);

        p1.initializeWorkerPosition(1, w1p1);
        p2.initializeWorkerPosition(1, w1p2);

        p1.setCurrentWorker(p1.getWorkers().get(0));

        w1p1.setLevel(Level.GROUND);
        w1p2.setLevel(Level.BOTTOM);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(3, 3))));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(w1p1.getPawn(), p2.getWorker(1));
        assertEquals(w1p2.getPawn(), p1.getWorker(1));
        assertEquals(Level.BOTTOM, p1.getWorker(1).getLevel());
        assertEquals(Level.GROUND, p2.getWorker(1).getLevel());
    }


    // Minotaur: Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.
    @Test
    void movingWithMinotaurTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Minotaur as God, he can move to an occupied cell and the opponent's worker is pushed back (if possible)
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block w1p1 = (Block) board.getCell(2, 2);
        Block w1p2 = (Block) board.getCell(3, 3);
        Block pushed = (Block) board.getCell(4, 4);


        p1.initializeWorkerPosition(1, w1p1);
        p2.initializeWorkerPosition(1, w1p2);

        p1.setCurrentWorker(p1.getWorkers().get(0));

        w1p1.setLevel(Level.GROUND);
        w1p2.setLevel(Level.BOTTOM);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.MINOTAUR);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(3, 3))));
        ReturnContent returnContent = game.gameEngine();

        // it checks that the move is possible and that the workers are correctly moved
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertNull(w1p1.getPawn());
        assertEquals(w1p2.getPawn(), p1.getWorker(1));
        assertEquals(pushed.getPawn(), p2.getWorker(1));
        assertEquals(Level.BOTTOM, p1.getWorker(1).getLevel());
        assertEquals(Level.GROUND, p2.getWorker(1).getLevel());
    }

    // TODO
    // Persephone: If possible, at least one Worker must move up this turn.
    // @Test
    void movingWithPersephoneMalusTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Apollo as God, he can move to an occupied cell and the workers ae swapped
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block w1p1 = (Block) board.getCell(3, 4);
        Block w1p2 = (Block) board.getCell(3, 3);

        p1.initializeWorkerPosition(1, w1p1);
        p2.initializeWorkerPosition(1, w1p2);

        p1.setCurrentWorker(p1.getWorkers().get(0));

        w1p1.setLevel(Level.BOTTOM);
        w1p2.setLevel(Level.GROUND);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.PERSEPHONE);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(3, 3))));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(w1p1.getPawn(), p2.getWorker(1));
        assertEquals(w1p2.getPawn(), p1.getWorker(1));
        assertEquals(Level.GROUND, p1.getWorker(1).getLevel());
        assertEquals(Level.BOTTOM, p2.getWorker(1).getLevel());
    }

    //
    // Triton: Each time your Worker moves into a perimeter space, it may immediately move again.
    // @Test
    void movingWithTritonTest() throws ParserConfigurationException, SAXException {
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
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block cell1 = (Block) board.getCell(0, 1);
        Block cell2 = (Block) board.getCell(0, 2);
        Block cell3 = (Block) board.getCell(1, 2);


        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cell1.setLevel(Level.BOTTOM);
        cell2.setLevel(Level.GROUND);
        cell3.setLevel(Level.GROUND);
        worker1Player1.setLevel(Level.GROUND);

        game.setState(State.MOVE);
        game.setCurrentPlayer(p1);
        game.assignCard(God.TRITON);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(0, 1))));
        //     game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(0, 2))));
        //      game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.USE_POWER, new ReducedDemandCell(1, 2))));

        ReturnContent returnContent = game.gameEngine();

        // check that the move is made correctly and the state is set to build
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cell1.getPawn(), p1.getCurrentWorker());
    }
}
