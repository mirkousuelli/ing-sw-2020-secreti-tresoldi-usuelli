package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChooseCardTest {

    @Test
    void chooseGodTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        List<God> chosenGods = new ArrayList<>();
        chosenGods.add(God.ARTEMIS);
        chosenGods.add(God.MINOTAUR);
        chosenGods.add(God.PERSEPHONE);

        game.getDeck().fetchDeck();
        game.setChosenGods(chosenGods);

        game.setCurrentPlayer(p2);
        game.setState(State.CHOOSE_CARD);

        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.ARTEMIS)));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(God.ARTEMIS, p2.getCard().getGod());
        assertEquals(p3, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());

        game.setState(State.CHOOSE_CARD);
        game.setRequest(new ActionToPerform<>(p3.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.PERSEPHONE)));
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(God.PERSEPHONE, p3.getCard().getGod());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());

        game.setState(State.CHOOSE_CARD);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.MINOTAUR)));
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(God.MINOTAUR, p1.getCard().getGod());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_STARTER, returnContent.getState());
    }


    @Test
    void wrongPickGodTest() throws ParserConfigurationException, SAXException {

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        game.addPlayer(p1);
        game.addPlayer(p2);

        List<God> chosenGods = new ArrayList<>();
        chosenGods.add(God.ATLAS);
        chosenGods.add(God.PAN);
        game.getDeck().fetchDeck();
        game.setChosenGods(chosenGods);

        game.setCurrentPlayer(p2);
        game.setState(State.CHOOSE_CARD);
        game.setRequest(new ActionToPerform<>(p2.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.ATLAS)));
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(God.ATLAS, p2.getCard().getGod());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());

        game.setState(State.CHOOSE_CARD);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.ZEUS)));
        returnContent = game.gameEngine();

        //since the player picked a God that isn't in the deck, he has to choose again
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertThrows(NullPointerException.class, () -> p1.getCard().getGod());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());

        game.setState(State.CHOOSE_CARD);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.ATLAS)));
        returnContent = game.gameEngine();

        //since the player picked a God that has already been chosen, he has to choose again
        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertThrows(NullPointerException.class, () -> p1.getCard().getGod());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_CARD, returnContent.getState());

        game.setState(State.CHOOSE_CARD);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_CARD, God.PAN)));
        returnContent = game.gameEngine();

        //after the player picks the last God, the state is set to Choose Starter
        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(God.PAN, p1.getCard().getGod());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(State.CHOOSE_STARTER, returnContent.getState());
    }
}
