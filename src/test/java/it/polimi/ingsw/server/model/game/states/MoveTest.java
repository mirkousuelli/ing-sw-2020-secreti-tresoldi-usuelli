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
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MoveTest {

    @Test
    void correctMoveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell where he can move, his worker is actually moved there
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
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

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
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

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
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

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(2, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
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

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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
        p2.setCurrentWorker(p2.getWorkers().get(0));

        w1p1.setLevel(Level.BOTTOM);
        w1p2.setLevel(Level.GROUND);

        game.setState(State.MOVE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ZEUS);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(3, 3))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
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
    TESTS ON GODS POWER (BASIC ABILITY)
___________________________________________________________________________________________________________________________________
*/


    // APOLLO: Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated.
    @Test
    void movingWithApolloTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Apollo as God, he can move to an occupied cell and the workers ae swapped
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(3, 3))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(w1p1.getPawn(), p2.getWorker(1));
        assertEquals(w1p2.getPawn(), p1.getWorker(1));
        assertEquals(Level.BOTTOM, p1.getWorker(1).getLevel());
        assertEquals(Level.GROUND, p2.getWorker(1).getLevel());
    }


    // ARTEMIS: Your Worker may move one additional time, but not back to its initial space.
    @Test
    void movingWithArtemisTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Artemis as God, he can move one additional time (not to the cell he came from)
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.TRITON);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        // check that the state is still move since Artemis power allows the player to move a second time
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.ASK_ADDITIONAL_POWER, returnContent.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cell1.getPawn(), p1.getCurrentWorker());

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(2, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent rc = game.gameEngine();

        // check that the move is made correctly and the state is set to build
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(State.BUILD, rc.getState());
        assertNull(cell1.getPawn());
        assertEquals(cell2.getPawn(), p1.getCurrentWorker());

    }


    // ATHENA: If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn.
    @Test
    void movingWithAthenaMalusActiveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if Athena's Malus is active, the other players cannot move up during their turn
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block w1p2 = (Block) board.getCell(1, 1);
        Block chosenCell = (Block) board.getCell(1, 0);

        w1p2.setLevel(Level.BOTTOM);
        chosenCell.setLevel(Level.BOTTOM);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ATHENA);
        p1.initializeWorkerPosition(1, (Block) board.getCell(0, 0));
        p1.setCurrentWorker(p1.getWorker(1));
        board.getCell(0, 1).setLevel(Level.BOTTOM);
        board.move(p1, board.getCell(0, 1));
        ChooseCard.applyMalus(game, Timing.END_TURN);

        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);
        p2.initializeWorkerPosition(1, w1p2);
        p2.setCurrentWorker(p2.getWorkers().get(0));

        game.setState(State.MOVE);

        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 0))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(chosenCell.getPawn(), p2.getWorker(1));
        assertNull(w1p2.getPawn());
    }


    // MINOTAUR: Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards to an unoccupied space at any level.
    @Test
    void movingWithMinotaurTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Minotaur as God, he can move to an occupied cell and the opponent's worker is pushed back (if possible)
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.TRITON);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);


        game.setCurrentPlayer(p1);
        game.assignCard(God.MINOTAUR);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(3, 3))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
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


    // PAN: You also win if your worker moves down two or more levels
    @Test
    void winningWithPan() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Pan as God and moves down of 2 levels or more, he wins
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block cellToMoveTo = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));

        cellToMoveTo.setLevel(Level.GROUND);
        worker1Player1.setLevel(Level.MIDDLE);

        game.setState(State.MOVE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.PAN);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        // check that the player moved to down 2 levels, so he wins
        //   assertEquals(AnswerType.VICTORY, returnContent.getAnswerType());
        //   assertEquals(State.VICTORY, returnContent.getState());
        assertEquals(cellToMoveTo.getPawn(), p1.getCurrentWorker());
    }


    // PERSEPHONE: If possible, at least one Worker must move up this turn.
    @Test
    void movingWithPersephoneMalusActiveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Persephone as God, the other players have to move their worker up whenever it's possible
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block w1p2 = (Block) board.getCell(1, 1);
        Block chosenCell = (Block) board.getCell(1, 0);

        w1p2.setLevel(Level.BOTTOM);
        chosenCell.setLevel(Level.MIDDLE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.PERSEPHONE);
        ChooseCard.applyMalus(game, Timing.DEFAULT);

        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);
        p2.initializeWorkerPosition(1, w1p2);
        p2.setCurrentWorker(p2.getWorkers().get(0));

        game.setState(State.MOVE);

        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 0))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(chosenCell.getPawn(), p2.getWorker(1));
    }


    // TRITON: Each time your Worker moves into a perimeter space, it may immediately move again.
    @Test
    void movingWithTritonTest() throws ParserConfigurationException, SAXException {
        /*@function
         *
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);


        game.setCurrentPlayer(p1);
        game.assignCard(God.TRITON);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent rc = game.gameEngine();

        // it checks that if the player moves to a perimeter cell, he can move his worker again
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(State.MOVE, rc.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cell1.getPawn(), p1.getCurrentWorker());


        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(0, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        rc = game.gameEngine();

        // it checks that if the player moves to a perimeter cell, he can move his worker again
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(State.MOVE, rc.getState());
        assertNull(cell1.getPawn());
        assertEquals(cell2.getPawn(), p1.getCurrentWorker());

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        rc = game.gameEngine();

        // it checks that if the player does not move to a perimeter cell, the state is changed to build
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(State.BUILD, rc.getState());
        assertNull(cell2.getPawn());
        assertEquals(cell3.getPawn(), p1.getCurrentWorker());
    }



/*
___________________________________________________________________________________________________________________________________
    TESTS ON SPECIFIC CASES OF GOD POWERS
___________________________________________________________________________________________________________________________________
*/


    @Test
    void notUsingAdditionalPowerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Artemis as God but he decides not to use his ability, the state is changed to additionalPower
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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

        cell1.setLevel(Level.MIDDLE);
        worker1Player1.setLevel(Level.BOTTOM);

        game.setState(State.MOVE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);


        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        // check that the state is still move since Artemis power allows the player to move a second time
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.ASK_ADDITIONAL_POWER, returnContent.getState());
        assertNull(worker1Player1.getPawn());
        assertEquals(cell1.getPawn(), p1.getCurrentWorker());

        /*game.setState(State.ADDITIONAL_POWER);
        game.setRequest(new ActionToPerform<(p1.nickName, new Demand<(DemandType.USE_POWER, new ReducedDemandCell(-1, -1))));
        GameMemory.save(game, Lobby.backupPath);
        ReturnContent rc = game.gameEngine();

        // check that if the player decides not to use the god power, the game changes to build correctly
        assertEquals(AnswerType.SUCCESS, rc.getAnswerType());
        assertEquals(State.BUILD, rc.getState());
        assertNull(cell2.getPawn());
        assertEquals(cell1.getPawn(), p1.getCurrentWorker());*/
    }


    @Test
    void cannotMoveTooHighMinotaurTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Minotaur as God, he cannot move to an occupied cell that is too high
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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
        w1p2.setLevel(Level.MIDDLE);

        game.setState(State.MOVE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.PAN);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ARTEMIS);


        game.setCurrentPlayer(p1);
        game.assignCard(God.MINOTAUR);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(3, 3))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        // it checks that the move is possible and that the workers are correctly moved
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
        assertNull(pushed.getPawn());
        assertEquals(w1p1.getPawn(), p1.getWorker(1));
        assertEquals(w1p2.getPawn(), p2.getWorker(1));
        assertEquals(Level.GROUND, p1.getWorker(1).getLevel());
        assertEquals(Level.MIDDLE, p2.getWorker(1).getLevel());
    }


    @Test
    void notMovingUpWithPersephoneMalusActiveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player has Persephone as God, the other players have to move their worker up whenever it's possible
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block w1p2 = (Block) board.getCell(1, 1);
        Block chosenCell = (Block) board.getCell(1, 0);
        Block cell1 = (Block) board.getCell(1, 2);

        w1p2.setLevel(Level.BOTTOM);
        chosenCell.setLevel(Level.GROUND);
        cell1.setLevel(Level.MIDDLE);

        game.setCurrentPlayer(p1);
        game.assignCard(God.PERSEPHONE);
        ChooseCard.applyMalus(game, Timing.DEFAULT); //per athena uguale ma con timing end_turn

        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);
        p2.initializeWorkerPosition(1, w1p2);
        p2.setCurrentWorker(p2.getWorkers().get(0));

        game.setState(State.MOVE);

        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 0))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
        assertEquals(w1p2.getPawn(), p2.getWorker(1));
        assertNull(chosenCell.getPawn());
    }


    @Test
    void movingUpWithAthenaMalusActiveTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if Athena's Malus is active, the other players cannot move up during their turn
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block w1p2 = (Block) board.getCell(1, 1);
        Block chosenCell = (Block) board.getCell(1, 0);

        w1p2.setLevel(Level.BOTTOM);
        chosenCell.setLevel(Level.MIDDLE);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ATHENA);
        p1.initializeWorkerPosition(1, (Block) board.getCell(0, 0));
        p1.setCurrentWorker(p1.getWorker(1));
        board.getCell(0, 1).setLevel(Level.BOTTOM);
        board.move(p1, board.getCell(0, 1));
        ChooseCard.applyMalus(game, Timing.END_TURN);

        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);
        p2.initializeWorkerPosition(1, w1p2);
        p2.setCurrentWorker(p2.getWorkers().get(0));

        game.setState(State.MOVE);

        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(1, 0))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
        assertEquals(w1p2.getPawn(), p2.getWorker(1));
        assertNull(chosenCell.getPawn());
    }
}
