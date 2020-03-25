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

import it.polimi.ingsw.model.cards.gods.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    /*@class
     * it contains a list of the 14 chosen cards
     */

    private List<Card> cards;

    public Deck() {
        /*@constructor
         * it creates a list of the 14 chosen cards
         */

        cards = new ArrayList<>();

        cards.add(new Apollo());
        cards.add(new Arthemis());
        cards.add(new Athena());
        cards.add(new Atlas());
        cards.add(new Chronus());
        cards.add(new Demeter());
        cards.add(new Hephaestus());
        cards.add(new Minotaur());
        cards.add(new Pan());
        cards.add(new Persephone());
        cards.add(new Poseidon());
        cards.add(new Prometheus());
        cards.add(new Triton());
        cards.add(new Zeus());
    }

    public Card popCard() {
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
}