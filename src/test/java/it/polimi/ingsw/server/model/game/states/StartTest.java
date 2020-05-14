package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDeck;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.network.message.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StartTest {

    @Test
    void correctSwitchToChooseCardTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        List<God> chosenGods = new ArrayList<>();
        chosenGods.add(God.APOLLO);
        chosenGods.add(God.HESTIA);
        chosenGods.add(God.ZEUS);

        game.getDeck().fetchCards(chosenGods);


        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.CHOOSE_CARD, new ReducedDeck(chosenGods).getReducedGodList())));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());
    }

    @Test
    void creationDeckTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        List<God> chosenGods = new ArrayList<>();
        chosenGods.add(God.APOLLO);
        chosenGods.add(God.HESTIA);
        chosenGods.add(God.ZEUS);

        game.getDeck().fetchCards(chosenGods);


        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.CHOOSE_CARD, new ReducedDeck(chosenGods).getReducedGodList())));
        ReturnContent returnContent = game.gameEngine();

        // it checks that the operation is made successfully and then changes to ChooseCard state
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());
        assertEquals(God.APOLLO.toString(),game.getDeck().popCard(God.APOLLO).getName());
        assertEquals(God.HESTIA.toString(),game.getDeck().popCard(God.HESTIA).getName());
        assertEquals(God.ZEUS.toString(),game.getDeck().popCard(God.ZEUS).getName());

        // if the god isn't in the deck it throws a NullPointerException
        assertThrows(NullPointerException.class , () -> game.getDeck().popCard(God.ATLAS).getName());
        assertThrows(NullPointerException.class , () -> game.getDeck().popCard(God.DEMETER).getName());
    }
}
