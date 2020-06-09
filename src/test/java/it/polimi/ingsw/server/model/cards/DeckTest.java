package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.server.model.cards.gods.God;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void fetchCardTest() throws ParserConfigurationException, SAXException {
        Deck deck = new Deck();

        God god = God.CHRONUS;

        deck.fetchCard(god);
    }

    @Test
    void fetchCardsTest() throws ParserConfigurationException, SAXException {
        Deck deck = new Deck();
        List<God> gods = new ArrayList<>();

        gods.add(God.APOLLO);
        gods.add(God.HESTIA);
        gods.add(God.ZEUS);

        deck.fetchCards(gods);
    }

    @Test
    void fetchDeckTest() throws ParserConfigurationException, SAXException {
        Deck deck = new Deck();

        deck.fetchDeck();
    }

    @Test
    void popAllCardsTest() throws ParserConfigurationException, SAXException {

        Deck deck = new Deck();
        List<ReducedCard> cards = deck.popAllGods(3);

        //check that the actual number of cards is 14
        assertEquals(14,cards.size());


        //now control that if I insert a wrong number of player, the cards aren't popped

        Deck deck2 = new Deck();
        List<ReducedCard> cards2 = deck2.popAllGods(4);

        //check that the actual number of cards is 0
        assertEquals(0,cards2.size());

    }
}