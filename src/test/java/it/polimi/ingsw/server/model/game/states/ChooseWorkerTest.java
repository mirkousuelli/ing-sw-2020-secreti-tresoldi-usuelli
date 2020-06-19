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
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;


public class ChooseWorkerTest {

    @Test
    void correctChosenWorkerTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl2");
        Player p2 = new Player("Pl1");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(3, 4);
        Block worker2Player1 = (Block) board.getCell(0, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        game.setState(State.CHOOSE_WORKER);

        game.setCurrentPlayer(p2);
        game.assignCard(God.APOLLO);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        assertEquals("chooseWorker",game.getState().getName());

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.MOVE,returnContent.getState());
        assertEquals(p1.getCurrentWorker(), game.getCurrentPlayer().getWorker(2));
        assertEquals(worker2Player1, p1.getCurrentWorker().getLocation());
    }

    @Test
    void pickedWrongCellTest() throws ParserConfigurationException, SAXException {
        // if the player picks a cell where there's not one of his workers, he has to pick again

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl2");
        Player p2 = new Player("Pl1");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(3, 4);
        Block worker2Player1 = (Block) board.getCell(0, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        game.setState(State.CHOOSE_WORKER);

        game.setCurrentPlayer(p2);
        game.assignCard(God.APOLLO);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(4, 0))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_WORKER,returnContent.getState());
    }


    @Test
    void cannotMoveInA2PlayersGameTest() throws ParserConfigurationException, SAXException {
        // check that if there are only two player and one of them cannot move any of his worker, the other one is the winner
        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(0, 1);
        Block block1 = (Block) board.getCell(1, 0);
        Block block2 = (Block) board.getCell(1, 1);
        Block block3 = (Block) board.getCell(1, 2);
        Block block4 = (Block) board.getCell(0, 2);
        block1.setLevel(Level.MIDDLE);
        block2.setLevel(Level.DOME);
        block3.setLevel(Level.TOP);
        block4.setLevel(Level.MIDDLE);


        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        game.setState(State.CHOOSE_WORKER);

        game.setCurrentPlayer(p2);
        game.assignCard(God.CHRONUS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.PAN);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.VICTORY, returnContent.getAnswerType());
        assertEquals(State.VICTORY,returnContent.getState());
    }

    @Test
    void cannotMoveInA3PlayersGameTest() throws ParserConfigurationException, SAXException {
        /* Check that if there are three player and if one them cannot move any of his worker, he loses and is eliminated
         * while the other two continue to play
         */
        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        Player p3 = new Player("Pl3");

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);


        Board board = game.getBoard();
        Block worker1Player3 = (Block) board.getCell(0, 0);
        Block worker2Player3 = (Block) board.getCell(0, 1);
        Block block1 = (Block) board.getCell(1, 0);
        Block block2 = (Block) board.getCell(1, 1);
        Block block3 = (Block) board.getCell(1, 2);
        Block block4 = (Block) board.getCell(0, 2);
        block1.setLevel(Level.MIDDLE);
        block2.setLevel(Level.DOME);
        block3.setLevel(Level.TOP);
        block4.setLevel(Level.MIDDLE);


        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);

        game.setState(State.CHOOSE_WORKER);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ZEUS);

        game.setCurrentPlayer(p2);
        game.assignCard(God.CHRONUS);

        game.setCurrentPlayer(p3);
        game.assignCard(God.PAN);

        game.setRequest(new ActionToPerform<>(p3.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        // the player is defeated
        assertEquals(AnswerType.DEFEAT, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_WORKER,returnContent.getState());

        // the player is removed from the game (with his workers)
        assertEquals(2,game.getNumPlayers());
        assertEquals(0, p3.getWorkers().size());

        // the other players keep playing the match and the current player is switched correctly
        assertEquals(p1, game.getCurrentPlayer());

    }
}
