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

/*Power:
 *Each time your Worker moves into a perimeter space, it may immediately move again
 */

public class Triton extends Card {
    /*@class
     * it portrays the power of Triton
     */

    public Triton() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.TRITON, Effect.MOVE);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*@function
         * it implements Triton's power
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