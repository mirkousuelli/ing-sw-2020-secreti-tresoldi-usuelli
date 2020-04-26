/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.cards;

import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.gods.GodParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.*;

public class Deck {
    /*@class
     * it contains a list of the 14 chosen cards
     */

    private final List<Card> cards;
    private final GodParser parser;

    public Deck() throws ParserConfigurationException, SAXException {
        /*@constructor
         * it creates a list of the 14 chosen cards
         */

        parser = new GodParser(this);
        cards = new ArrayList<>();
    }

    public Card popRandomCard() {
        /*@function
         * it picks a card from the deck
         */

        Random randomIndex = new Random();
        Card pickedCard = null;

        if (cards.size() != 0) {
            pickedCard= cards.get(randomIndex.nextInt(cards.size()));
            cards.remove(pickedCard);
        }

        return pickedCard;
    }

    public void addCard(Card newCard) {
        this.cards.add(newCard);
    }

    public void fetchCard(God god) {
        parser.parseCards(Collections.singletonList(god));
    }

    public void fetchCards(List<God> gods) {
        parser.parseCards(gods);
    }

    public void fetchDeck() {
        parser.parseCards(Arrays.asList(God.values()));
    }

    public Card popCard(God god) {
        /*@function
         * it picks the selected card from the deck
         */

        Card pickedCard = null;

        if (cards.size() != 0) {
            for (Card card : cards) {
                if (card.getGod().equals(god)){
                    pickedCard = card;
                    cards.remove(pickedCard);
                }
            }
        }

        return pickedCard;
    }
}