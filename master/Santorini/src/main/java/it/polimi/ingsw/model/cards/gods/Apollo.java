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
 *  Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
 */

public class Apollo extends Card {
    /*@class
     * it portrays the power of Apollo
     */

    public Apollo() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.APOLLO, Effect.MOVE);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*@function
         * it implements Apollo's power
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