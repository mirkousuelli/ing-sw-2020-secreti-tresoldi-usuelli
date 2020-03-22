/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

/*Power
 *  Your Worker may build one additional block (not dome) on top of your first block
 */

public class Hephaestus extends Card {
    /*@class
     * it portrays the power of Hephaestus
     */

    public Hephaestus() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.HEPHAESTUS, Effect.BUILD);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*@function
         * it implements Hephaestus' power
         */
        return true;
    }

    @Override
    public boolean usePower(List<Player> opponents) {
        /*@function
         * Unused
         */
        return true;
    }

    @Override
    public boolean usePower() {
        /*@function
         * Unused
         */
        return true;
    }
}