package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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

        assertEquals("chooseWorker", game.getState().getName());

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(0, 1))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.MOVE, returnContent.getState());
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
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
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
        assertEquals(State.VICTORY, returnContent.getState());
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
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());

        // the player is removed from the game (with his workers)
        assertEquals(2, game.getNumPlayers());
        assertEquals(0, p3.getWorkers().size());

        // the other players keep playing the match and the current player is switched correctly
        assertEquals(p1, game.getCurrentPlayer());

    }

    @Test
    void gettingStuckTest() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(0, 1);
        Block worker2Player1 = (Block) board.getCell(2, 1);

        Block worker1Player2 = (Block) board.getCell(1, 0);
        Block worker2Player2 = (Block) board.getCell(2, 3);

        Block completeTower1 = (Block) board.getCell(1, 1);
        Block completeTower2 = (Block) board.getCell(2, 2);
        Block completeTower3 = (Block) board.getCell(3, 2);
        Block completeTower4 = (Block) board.getCell(3, 1);
        Block completeTower5 = (Block) board.getCell(3, 0);
        Block middleTower1 = (Block) board.getCell(2, 0);

        Block newPos = (Block) board.getCell(1, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);
        p1.setCurrentWorker(p1.getWorker(2));

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        //set state
        game.setState(State.CHOOSE_WORKER);

        //set cell's level
        completeTower1.setPreviousLevel(Level.TOP);
        completeTower1.setLevel(Level.DOME);

        completeTower2.setPreviousLevel(Level.TOP);
        completeTower2.setLevel(Level.DOME);

        completeTower3.setPreviousLevel(Level.TOP);
        completeTower3.setLevel(Level.DOME);

        completeTower4.setPreviousLevel(Level.TOP);
        completeTower4.setLevel(Level.DOME);

        completeTower5.setPreviousLevel(Level.TOP);
        completeTower5.setLevel(Level.DOME);

        middleTower1.setPreviousLevel(Level.BOTTOM);
        middleTower1.setLevel(Level.MIDDLE);

        worker1Player1.setPreviousLevel(Level.BOTTOM);
        worker1Player1.setLevel(Level.MIDDLE);

        worker2Player1.setPreviousLevel(Level.BOTTOM);
        worker2Player1.setLevel(Level.MIDDLE);

        worker1Player2.setPreviousLevel(Level.GROUND);
        worker1Player2.setLevel(Level.BOTTOM);

        newPos.setPreviousLevel(Level.BOTTOM);
        newPos.setLevel(Level.MIDDLE);


        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker2Player1.getX(), worker2Player1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);

        ReturnContent returnContent = game.gameEngine();
        List<ReducedAnswerCell> payload = (List<ReducedAnswerCell>) returnContent.getPayload();

        assertEquals(3, payload.size());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, newPos.getX(), newPos.getY(), ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, middleTower1.getX(), middleTower1.getY(), ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, worker2Player1.getX(), worker2Player1.getY(), ReducedAction.DEFAULT)).count());

    }

    @Test
    void gettingStuck1Test() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block worker2Player1 = (Block) board.getCell(2, 1);

        Block worker1Player2 = (Block) board.getCell(2, 2);
        Block worker2Player2 = (Block) board.getCell(3, 1);

        Block completeTower1 = (Block) board.getCell(1, 1);
        Block completeTower2 = (Block) board.getCell(3, 0);
        Block completeTower3 = (Block) board.getCell(3, 2);
        Block middleTower1 = (Block) board.getCell(2, 0);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        //set state
        game.setState(State.CHOOSE_WORKER);

        //set cell's level
        completeTower1.setPreviousLevel(Level.TOP);
        completeTower1.setLevel(Level.DOME);

        completeTower2.setPreviousLevel(Level.TOP);
        completeTower2.setLevel(Level.DOME);

        completeTower3.setPreviousLevel(Level.TOP);
        completeTower3.setLevel(Level.DOME);

        middleTower1.setPreviousLevel(Level.BOTTOM);
        middleTower1.setLevel(Level.MIDDLE);

        worker1Player1.setPreviousLevel(Level.GROUND);
        worker1Player1.setLevel(Level.BOTTOM);

        worker2Player1.setPreviousLevel(Level.BOTTOM);
        worker2Player1.setLevel(Level.MIDDLE);

        worker2Player2.setPreviousLevel(Level.GROUND);
        worker2Player2.setLevel(Level.BOTTOM);


        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker1Player1.getX(), worker1Player1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);

        ReturnContent returnContent = game.gameEngine();
        List<ReducedAnswerCell> payload = (List<ReducedAnswerCell>) returnContent.getPayload();

        assertEquals(4, payload.size());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, 0, 0, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, middleTower1.getX(), middleTower1.getY(), ReducedAction.MOVE)).count()); //2,0
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, 0, 1, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, worker1Player1.getX(), worker1Player1.getY(), ReducedAction.DEFAULT)).count());


        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker2Player1.getX(), worker2Player1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);

        returnContent = game.gameEngine();
        payload = (List<ReducedAnswerCell>) returnContent.getPayload();

        assertEquals(3, payload.size());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, middleTower1.getX(), middleTower1.getY(), ReducedAction.MOVE)).count()); //2,0
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, 1, 2, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> PreparePayloadTest.checkReducedAnswerCell(rac, worker2Player1.getX(), worker2Player1.getY(), ReducedAction.DEFAULT)).count());
    }

    @Test
    void oneWorkerBlockedTest() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);

        //assign a god everyone in the game
        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        game.setCurrentPlayer(p2);
        game.assignCard(God.ATLAS);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(3, 0);
        Block worker2Player1 = (Block) board.getCell(2, 4);

        Block worker1Player2 = (Block) board.getCell(4, 0);
        Block worker2Player2 = (Block) board.getCell(3, 3);

        Block middleTower1 = (Block) board.getCell(2, 1);
        Block middleTower3 = (Block) board.getCell(3, 1);
        Block middleTower2 = (Block) board.getCell(4, 1);
        Block middleTower4 = (Block) board.getCell(2, 2);
        Block bottomTower1 = (Block) board.getCell(3, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        //set state
        game.setState(State.CHOOSE_WORKER);

        //set cell's level
        middleTower1.setPreviousLevel(Level.BOTTOM);
        middleTower1.setLevel(Level.MIDDLE);

        middleTower2.setPreviousLevel(Level.BOTTOM);
        middleTower2.setLevel(Level.MIDDLE);

        middleTower3.setPreviousLevel(Level.BOTTOM);
        middleTower3.setLevel(Level.MIDDLE);

        middleTower4.setPreviousLevel(Level.BOTTOM);
        middleTower4.setLevel(Level.MIDDLE);

        bottomTower1.setPreviousLevel(Level.GROUND);
        bottomTower1.setLevel(Level.BOTTOM);

        worker1Player1.setPreviousLevel(Level.GROUND);
        worker1Player1.setLevel(Level.BOTTOM);


        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker1Player2.getX(), worker1Player2.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);

        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
    }
}
