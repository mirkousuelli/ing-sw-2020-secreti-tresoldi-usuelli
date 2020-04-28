package it.polimi.ingsw.server.model.cards;

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

        God god = God.APOLLO;

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
}