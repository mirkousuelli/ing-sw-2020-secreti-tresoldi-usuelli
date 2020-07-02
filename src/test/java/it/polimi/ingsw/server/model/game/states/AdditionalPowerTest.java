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

public class AdditionalPowerTest {

    @Test
    void pickedImpossibleCellTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cell1 = (Block) board.getCell(1, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        cell1.setLevel(Level.MIDDLE);

        game.setState(State.ADDITIONAL_POWER);
        game.setPrevState(State.BUILD);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(1, 6))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());
        assertEquals(Level.MIDDLE, cell1.getLevel());
    }


    @Test
    void usingBuildAdditionalPowerTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cell1 = (Block) board.getCell(1, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        cell1.setLevel(Level.MIDDLE);

        game.setState(State.ADDITIONAL_POWER);
        game.setPrevState(State.BUILD);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        game.setAllowedActions(PreparePayload.preparePayloadBuild(game, Timing.ADDITIONAL, State.BUILD));
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(1, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
        assertEquals(Level.TOP, cell1.getLevel());
    }


    @Test
    void errorUsingBuildAdditionalPowerTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cell1 = (Block) board.getCell(1, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        cell1.setLevel(Level.DOME);

        game.setState(State.ADDITIONAL_POWER);
        game.setPrevState(State.BUILD);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.HESTIA);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(1, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.ADDITIONAL_POWER, returnContent.getState());
        assertEquals(Level.DOME, cell1.getLevel());
    }


    @Test
    void usingMoveAdditionalPowerTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cell1 = (Block) board.getCell(1, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        cell1.setLevel(Level.BOTTOM);

        game.setState(State.ADDITIONAL_POWER);
        game.setPrevState(State.MOVE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        game.setAllowedActions(PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.MOVE));
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(1, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(p1.getWorker(1), cell1.getPawn());
    }

    @Test
    void winningWithAdditionalPowerTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        game.addPlayer(p1);
        game.addPlayer(p2);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block cell1 = (Block) board.getCell(1, 2);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorker(1));

        worker1Player1.setLevel(Level.MIDDLE);
        cell1.setLevel(Level.TOP);

        game.setState(State.ADDITIONAL_POWER);
        game.setPrevState(State.MOVE);

        game.setCurrentPlayer(p2);
        game.assignCard(God.MINOTAUR);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        game.setAllowedActions(PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.MOVE));
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(1, 2))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(AnswerType.VICTORY, returnContent.getAnswerType());
        assertEquals(State.VICTORY, returnContent.getState());
        assertEquals(p1.getWorker(1), cell1.getPawn());
    }

}
