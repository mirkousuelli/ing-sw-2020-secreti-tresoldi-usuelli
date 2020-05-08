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
import it.polimi.ingsw.server.model.cards.powers.Power;

import java.util.ArrayList;
import java.util.List;

public class Card implements Cloneable {
    /*@class
     * it describes the player's card
     */

    private God god;
    private String description;
    private int numPlayer;
    private List<Power> powerList;

    public Card() {
        /*@constructor
         * it sets the attributes describing the card
         */

        powerList = new ArrayList<>();
    }

    public String getName() {
        return god.toString();
    }

    public void setName(String name) {
        this.god = God.parseString(name);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumPlayer() {
        return numPlayer;
    }

    public void setNumPlayer(int numPlayer) {
        this.numPlayer = numPlayer;
    }

    public Power getPower(int i) {
        return powerList.get(i);
    }

    public void addPower(Power power) {
        powerList.add(power);
    }

    public God getGod() {
        return god;
    }

    public void setGod(God newGod) {
        this.god = newGod;
    }

    public List<Power> getPowerList() {
        return powerList;
    }

    private void setPowerList(List<Power> powerList) {
        this.powerList = powerList;
    }

    @Override
    public Card clone() {
        Card newCard = new Card();

        newCard.setGod(this.god);
        newCard.setDescription(this.description);
        newCard.setPowerList(this.getPowerList());

        return newCard;
    }
}