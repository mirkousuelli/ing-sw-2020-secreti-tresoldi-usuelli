package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class PlaceWorkersTest {


    @Test
    void correctWorkersPlacedTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("pl1");
        Player p2 = new Player("pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        List<ReducedWorker> workersPlayer1 = new ArrayList<>();
        ReducedWorker w1p1 = new ReducedWorker(p1.nickName, 0,0, false);
        ReducedWorker w2p1 = new ReducedWorker(p1.nickName, 2,4, true);
        workersPlayer1.add(w1p1);
        workersPlayer1.add(w2p1);

        List<ReducedWorker> workersPlayer2 = new ArrayList<>();
        ReducedWorker w1p2 = new ReducedWorker(p2.nickName, 1,3, false);
        ReducedWorker w2p2 = new ReducedWorker(p2.nickName, 2,2, true);
        workersPlayer2.add(w1p2);
        workersPlayer2.add(w2p2);

        game.setStarter(0);
        game.setCurrentPlayer(game.getPlayer(game.getStarter()));
        game.setState(State.PLACE_WORKERS);

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.PLACE_WORKERS, workersPlayer1)));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());


        game.setState(State.PLACE_WORKERS);
        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.PLACE_WORKERS, workersPlayer2)));
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());

        // check that the picked cells actually have workers, which means that they are not free
        assertFalse(game.getBoard().getCell(0,0).isFree());
        assertFalse(game.getBoard().getCell(2,2).isFree());
        assertFalse(game.getBoard().getCell(1,3).isFree());
        assertFalse(game.getBoard().getCell(2,4).isFree());

        // check that any different cell has no workers, which means that it is free
        assertTrue(game.getBoard().getCell(1,1).isFree());
        assertTrue(game.getBoard().getCell(4,0).isFree());
        assertTrue(game.getBoard().getCell(2,3).isFree());

    }


    @Test
    void pickingBusyCellTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("pl1");
        Player p2 = new Player("pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        List<ReducedWorker> workersPlayer1 = new ArrayList<>();
        ReducedWorker w1p1 = new ReducedWorker(p1.nickName, 0,0, false);
        ReducedWorker w2p1 = new ReducedWorker(p1.nickName, 2,4, true);
        workersPlayer1.add(w1p1);
        workersPlayer1.add(w2p1);

        List<ReducedWorker> workersPlayer2 = new ArrayList<>();
        ReducedWorker w1p2 = new ReducedWorker(p2.nickName, 1,3, false);
        ReducedWorker w2p2 = new ReducedWorker(p2.nickName, 0,0, true);
        workersPlayer2.add(w1p2);
        workersPlayer2.add(w2p2);

        game.setStarter(0);
        game.setCurrentPlayer(game.getPlayer(game.getStarter()));
        game.setState(State.PLACE_WORKERS);

        assertEquals("placeWorkers",game.getState().getName());

        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.PLACE_WORKERS, workersPlayer1)));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());

        game.setState(State.PLACE_WORKERS);
        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.PLACE_WORKERS, workersPlayer2)));
        returnContent = game.gameEngine();

        // since the player picked a busy cell, he has to place his workers again
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());
    }

}
