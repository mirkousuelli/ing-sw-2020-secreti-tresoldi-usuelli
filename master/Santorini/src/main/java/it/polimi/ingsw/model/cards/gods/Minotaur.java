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
 *  Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards
 *  to an unoccupied space at any level
 */

public class Minotaur extends Card {
    /*@class
     * it portrays the power of Minotaur
     */

    public Minotaur() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.MINOTAUR, Effect.MOVE);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*@function
         * it implements Minotaur's power
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