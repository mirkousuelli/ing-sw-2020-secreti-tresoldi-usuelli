package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


public class PlaceWorkersTest {


    @Test
    void correctWorkersPlacedTest() throws ParserConfigurationException, SAXException {
        Game game = new Game();
        Player p1 = new Player("pl1");
        Player p2 = new Player("pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        List<ReducedDemandCell> workersPlayer1 = new ArrayList<>();
        ReducedDemandCell w1p1 = new ReducedDemandCell(0,0);
        ReducedDemandCell w2p1 = new ReducedDemandCell(2,4);
        workersPlayer1.add(w1p1);
        workersPlayer1.add(w2p1);

        List<ReducedDemandCell> workersPlayer2 = new ArrayList<>();
        ReducedDemandCell w1p2 = new ReducedDemandCell(1,3);
        ReducedDemandCell w2p2 = new ReducedDemandCell(2,2);
        workersPlayer2.add(w1p2);
        workersPlayer2.add(w2p2);

  /*      Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(2, 4);
        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        Block worker1Player2 = (Block) board.getCell(1, 3);
        Block worker2Player2 = (Block) board.getCell(2, 2);
        p1.initializeWorkerPosition(1, worker1Player2);
        p1.initializeWorkerPosition(2, worker2Player2);
*/

        game.setStarter(0);
        game.setCurrentPlayer(game.getPlayer(game.getStarter()));
        game.setState(State.PLACE_WORKERS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.PLACE_WORKERS, workersPlayer1)));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());


        game.setState(State.PLACE_WORKERS);
        game.setRequest(new ActionToPerform(p2.nickName, new Demand(DemandType.PLACE_WORKERS, workersPlayer2)));
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
        // TODO check that the cells actually have workers
    }


    @Test
    void pickingBusyCellTest() throws ParserConfigurationException, SAXException {
        Game game = new Game();
        Player p1 = new Player("pl1");
        Player p2 = new Player("pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        List<ReducedDemandCell> workersPlayer1 = new ArrayList<>();
        ReducedDemandCell w1p1 = new ReducedDemandCell(0,0);
        ReducedDemandCell w2p1 = new ReducedDemandCell(2,4);
        workersPlayer1.add(w1p1);
        workersPlayer1.add(w2p1);

        List<ReducedDemandCell> workersPlayer2 = new ArrayList<>();
        ReducedDemandCell w1p2 = new ReducedDemandCell(1,3);
        ReducedDemandCell w2p2 = new ReducedDemandCell(0,0);
        workersPlayer2.add(w1p2);
        workersPlayer2.add(w2p2);

        game.setStarter(0);
        game.setCurrentPlayer(game.getPlayer(game.getStarter()));
        game.setState(State.PLACE_WORKERS);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.PLACE_WORKERS, workersPlayer1)));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());

        game.setState(State.PLACE_WORKERS);
        game.setRequest(new ActionToPerform(p2.nickName, new Demand(DemandType.PLACE_WORKERS, workersPlayer2)));
        returnContent = game.gameEngine();

        // since the player picked a busy cell, he has to place his workers again
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());
    }

}
