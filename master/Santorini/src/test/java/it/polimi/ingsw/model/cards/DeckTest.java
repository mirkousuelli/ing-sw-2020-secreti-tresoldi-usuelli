package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    //@Test
    void testPopRandomCard() throws ParserConfigurationException, SAXException {
        /*@function
         * it picks random cards
         */
        Deck deck = new Deck();
        List<Card> cards = new ArrayList<>();

        do {
            cards.add(deck.popRandomCard());
        }while (cards.size() < 14);

        for (int i = 0; i < cards.size(); i++) {
            for (int j = 0; j < cards.size(); j++) {
                if (i != j)
                    assertNotSame(cards.get(i), cards.get(j));
            }
        }

        assertNull(deck.popRandomCard());
    }
}