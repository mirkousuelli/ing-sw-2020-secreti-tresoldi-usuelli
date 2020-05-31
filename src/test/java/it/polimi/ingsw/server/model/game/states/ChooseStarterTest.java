package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;
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

public class ChooseStarterTest {

    @Test
    void correctChosenStarterTest() throws ParserConfigurationException, SAXException {
        // if the Challenger picks a correct name, the state changes to place workers and the current player is the starter
        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.CHOOSE_STARTER);

        assertEquals("chooseStarter",game.getState().getName());

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.CHOOSE_STARTER, new ReducedMessage("Fabio"))));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.PLACE_WORKERS, returnContent.getState());
        assertEquals(game.getPlayerList().indexOf(p1), game.getStarter());
        assertEquals("Fabio", game.getCurrentPlayer().getNickName());
    }


    @Test
    void choosingWrongNicknameTest() throws ParserConfigurationException, SAXException {
        // if the Challenger picks the nickname of someone not in the game, he has to choose again
        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setState(State.CHOOSE_STARTER);

        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.CHOOSE_STARTER, new ReducedMessage("Pippo"))));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_STARTER, returnContent.getState());
        assertEquals( -1, game.getStarter());
    }
}
