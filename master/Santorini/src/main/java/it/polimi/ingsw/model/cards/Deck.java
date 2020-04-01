/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.cards.xml.ParserXML;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    /*@class
     * it contains a list of the 14 chosen cards
     */

    private List<Card> cards;
    private ParserXML parser;

    public Deck() throws ParserConfigurationException, SAXException {
        /*@constructor
         * it creates a list of the 14 chosen cards
         */

        cards = new ArrayList<>();
        parser = new ParserXML(this);
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

    private void fetchCard(God god) {
        parser.parseCard(god);
    }

    private void fetchDeck() {
        parser.parseDeck();
    }

    /*public Card popCard(God god) {
        /*@function
         * it picks the selected card from the deck
         *

        Card pickedCard = null;

        if (cards.size() != 0) {
            for (Card card : cards) {
                if (card.god.equals(god)){
                    pickedCard = card;
                    cards.remove(pickedCard);
                }
            }
        }

        return pickedCard;
    }*/
}