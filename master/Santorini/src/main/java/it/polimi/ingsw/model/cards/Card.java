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

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.powers.Power;

public class Card {
    /*@class
     * it describes the player's card
     */

    private String name;
    private String description;
    private Power power;
    private Player owner;

    public Card() {
        /*@constructor
         * it sets the attributes describing the card
         */

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
        this.name = description;
    }

    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public Player getOwner() {
        /*@getter
         * it gets the owner of the card
         */
        return owner;
    }

    public void setOwner(Player owner) {
        /*@setter
         * it sets the owner of the card
         */
        this.owner = owner;
    }
}