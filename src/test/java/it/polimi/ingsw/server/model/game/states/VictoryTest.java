package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class VictoryTest {

    @Test
    void startNewGameAfterVictoryTest() throws ParserConfigurationException, SAXException {
        /*@function
         *  it checks that after a player won, the state is set back to start
         */
        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setCurrentPlayer(p1);
        game.setState(State.VICTORY);

        //it checks that the state is actually victory
        assertEquals("victory",game.getState().getName());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.START)));
        ReturnContent returnContent = game.gameEngine();

        // it checks that it correctly goes to the start
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType()); // the operation is made successfully
        assertEquals(State.START, returnContent.getState());
    }
}