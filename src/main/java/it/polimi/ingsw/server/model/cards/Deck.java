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

import it.polimi.ingsw.communication.message.payload.ReducedCard;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.gods.GodParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the deck and contains a list of the cards
 */
public class Deck {
    private final List<Card> cards;
    private final GodParser parser;

    /**
     * Constructor of the deck, that creates the list of the cards
     *
     * @throws ParserConfigurationException if there was a serious configuration error
     * @throws SAXException                 if the XML parser causes a basic error or a warning
     */
    public Deck() throws ParserConfigurationException, SAXException {
        parser = new GodParser(this);
        cards = new ArrayList<>();
    }

    /**
     * Method that adds a specific card to the list of cards
     *
     * @param newCard the card that is added
     */
    public void addCard(Card newCard) {
        this.cards.add(newCard);
    }

    public Card getCard(God god) {
        return cards.stream().filter(c -> c.getGod().equals(god)).reduce(null, (a, b) -> a != null ? a : b);
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

    /**
     * Method that picks the selected card from the deck
     *
     * @param god the God that is chosen
     * @return the picked card
     */
    public Card popCard(God god) {
        Card pickedCard = null;

        if (!cards.isEmpty()) {
            for (Card card : cards) {
                if (card.getGod().equals(god)) {
                    pickedCard = card.clone();
                    cards.remove(pickedCard);
                }
            }
        }

        return pickedCard;
    }

    /**
     * Method that picks all Gods from the deck
     *
     * @param numberOfPlayers the number of players in the game
     * @return the list of cards
     */
    public List<ReducedCard> popAllGods(int numberOfPlayers) {
        fetchDeck();

        return cards.stream().filter(c -> c.getNumPlayer() >= numberOfPlayers).map(ReducedCard::new).collect(Collectors.toList());
    }
}