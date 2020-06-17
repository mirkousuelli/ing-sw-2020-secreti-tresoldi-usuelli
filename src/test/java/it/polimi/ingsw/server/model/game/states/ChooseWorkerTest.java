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
        //assertNull(p1.getCurrentWorker());
    }

    // TODO cases where the player cannot move any of his workers or if he chooses one that cannot be moved
}
