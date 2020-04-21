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

import it.polimi.ingsw.server.model.cards.powers.Power;

import java.util.ArrayList;
import java.util.List;

public class Card {
    /*@class
     * it describes the player's card
     */

    private String name;
    private String description;
    private final List<Power> powerList;

    public Card() {
        /*@constructor
         * it sets the attributes describing the card
         */

        powerList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Power getPower(int i) {
        return powerList.get(i);
    }

    public void addPower(Power power) {
        powerList.add(power);
    }
}