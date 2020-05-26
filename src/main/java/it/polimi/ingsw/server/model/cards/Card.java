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

/**
 * Class that represents the Card and contains the corresponding God, a description, the number of player that it can
 * be played with and a list of powers: in our case the only God that has a restriction on the number of player is
 * Chronus, that can only be played in a 2-players match
 */
public class Card implements Cloneable {
    private God god;
    private String description;
    private int numPlayer;
    private List<Power> powerList;

    /**
     * Constructor of the card
     */
    public Card() {
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

    /**
     * Method that adds the power to the list of powers of the card
     *
     * @param power the power that is added
     */
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


    /**
     * Method that clones the card with his attributes (corresponding God, description and list of powers)
     *
     * @return the cloned card
     */
    @Override
    public Card clone() {
        Card newCard = new Card();

        newCard.setGod(this.god);
        newCard.setDescription(this.description);
        newCard.setPowerList(this.getPowerList());

        return newCard;
    }
}