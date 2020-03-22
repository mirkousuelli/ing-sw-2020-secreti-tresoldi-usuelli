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
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

public abstract class Card {
    /*@class
     * it describes the abstract object Card
     */

    public final God god;
    public final Effect effect;
    protected Player owner;

    public Card(God god, Effect effect) {
        /*@constructor
         * it sets the attributes describing the card
         */
        this.god = god;
        this.effect = effect;
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

    public abstract boolean usePower(Cell cell);

    public abstract boolean usePower(List<Player> opponents);

    public abstract boolean usePower();
}