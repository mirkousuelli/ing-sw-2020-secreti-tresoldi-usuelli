package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.game.states.PreparePayload;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ControllerTest {

    @Test
    void testMessage() throws ParserConfigurationException, SAXException {
        // if the current player makes a possible action, it is actually made

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Controller controller = lobby.getController();

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");

        Block worker1player1 = (Block) game.getBoard().getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1player1);
        player1.setCurrentWorker(player1.getWorker(1));

        ServerStub serverStub = new ServerStub();
        View player1View = new RemoteView(player1.nickName, serverStub);

        player1View.addObserver(controller);
        game.addObserver(player1View);

        game.addPlayer(player1);
        game.addPlayer(player2);

        game.setCurrentPlayer(player2);
        game.assignCard(God.APOLLO);

        game.setCurrentPlayer(player1);
        game.assignCard(God.DEMETER);
        game.setState(State.BUILD);

        GameMemory.save(game, Lobby.BACKUP_PATH);

        game.setAllowedActions(PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
        player1View.processMessage(new Demand<>(DemandType.BUILD, new ReducedDemandCell(1, 1)));

        assertEquals(player1, game.getCurrentPlayer());
        assertEquals(Level.BOTTOM, game.getBoard().getCell(1, 1).getLevel());
        assertEquals(Level.GROUND, ((Block) game.getBoard().getCell(1, 1)).getPreviousLevel());
        assertEquals(AnswerType.SUCCESS, serverStub.answer.getHeader());
        //assertEquals(DemandType.ASK_ADDITIONAL_POWER, serverStub.answer.getContext());
    }

    @Test
    void wrongPlayerMakingActionTest() throws ParserConfigurationException, SAXException {
        // A player cannot make an action (for example a move) while it's not his turn

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Controller controller = lobby.getController();

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");

        Block worker1player1 = (Block) game.getBoard().getCell(3, 1);

        player1.initializeWorkerPosition(1, worker1player1);
        player1.setCurrentWorker(player1.getWorker(1));

        ServerStub serverStub = new ServerStub();
        View player2View = new RemoteView(player2.nickName, serverStub);

        player2View.addObserver(controller);
        game.addObserver(player2View);

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.setCurrentPlayer(player1);
        game.setState(State.MOVE);

        player2View.processMessage(new Demand<>(DemandType.MOVE, new ReducedDemandCell(3, 1)));

        assertEquals(AnswerType.ERROR, serverStub.answer.getHeader());
        assertEquals(player1, game.getCurrentPlayer());
        assertEquals(Level.GROUND, game.getBoard().getCell(3, 1).getLevel());
        //assertEquals(DemandType.MOVE, serverStub.answer.getContext());
    }

    @Test
    void wrongActionTest() throws ParserConfigurationException, SAXException {
        // if a player has to make a specific action but tries to do another one, he is notified with an error (for example if he has to build but tries to move his worker)

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Controller controller = lobby.getController();

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");

        Block worker1player1 = (Block) game.getBoard().getCell(1, 4);
        Block chosenCell = (Block) game.getBoard().getCell(2, 4);

        player1.initializeWorkerPosition(1, worker1player1);
        player1.setCurrentWorker(player1.getWorker(1));

        ServerStub serverStub = new ServerStub();
        View player1View = new RemoteView(player1.nickName, serverStub);

        player1View.addObserver(controller);
        game.addObserver(player1View);

        game.addPlayer(player1);
        game.addPlayer(player2);
        game.setCurrentPlayer(player1);
        game.assignCard(God.APOLLO);
        game.setState(State.MOVE);

        player1View.processMessage(new Demand<>(DemandType.BUILD, new ReducedDemandCell(2, 4)));

        assertEquals(AnswerType.ERROR, serverStub.answer.getHeader());
        assertEquals(player1, game.getCurrentPlayer());
        //assertEquals(DemandType.BUILD, serverStub.answer.getContext());
        assertNull(chosenCell.getPawn());
        assertEquals(player1.getCurrentWorker(), worker1player1.getPawn());
    }

    @Test
    void impossibleActionTest() throws ParserConfigurationException, SAXException {
        // if a player make an impossible action (like moving too far from the current cell) he is notified with an error

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Controller controller = lobby.getController();

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");

        Block worker1player1 = (Block) game.getBoard().getCell(0, 0);
        Block cellToMoveTo = (Block) game.getBoard().getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1player1);
        player1.setCurrentWorker(player1.getWorker(1));

        ServerStub serverStub = new ServerStub();
        View player1View = new RemoteView(player1.nickName, serverStub);

        player1View.addObserver(controller);
        game.addObserver(player1View);

        game.addPlayer(player1);
        game.addPlayer(player2);

        game.setCurrentPlayer(player2);
        game.assignCard(God.APOLLO);

        game.setCurrentPlayer(player1);
        game.assignCard(God.DEMETER);
        game.setState(State.MOVE);

        GameMemory.save(game, Lobby.BACKUP_PATH);

        player1View.processMessage(new Demand<>(DemandType.MOVE, new ReducedDemandCell(2, 2)));

        assertEquals(AnswerType.ERROR, serverStub.answer.getHeader());
        assertEquals(player1, game.getCurrentPlayer());
        //assertEquals(DemandType.MOVE, serverStub.answer.getContext());
        assertNull(cellToMoveTo.getPawn());
        assertEquals(worker1player1.getPawn(), player1.getCurrentWorker());
    }
}