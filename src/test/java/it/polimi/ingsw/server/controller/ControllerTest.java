package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.states.Build;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.view.ActionToPerformView;
import it.polimi.ingsw.server.view.RemoteView;
import it.polimi.ingsw.server.view.View;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void testMessage() throws ParserConfigurationException, SAXException {
        Game game = new Game();
        Controller controller = new Controller(game);

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
        game.setCurrentPlayer(player1);
        game.assignCard(God.DEMETER);
        game.setState(new Build(game));

        player1View.processMessage(new Demand(DemandType.BUILD, new ReducedDemandCell(1,1)));

        assertEquals(player1, game.getCurrentPlayer());
        assertEquals(Level.BOTTOM, game.getBoard().getCell(1, 1).getLevel());
        assertEquals(Level.GROUND, ((Block) game.getBoard().getCell(1, 1)).getPreviousLevel());
        assertEquals(AnswerType.SUCCESS, serverStub.answer.getHeader());
        assertEquals(DemandType.ASK_ADDITIONAL_POWER, serverStub.answer.getContext());
    }
}