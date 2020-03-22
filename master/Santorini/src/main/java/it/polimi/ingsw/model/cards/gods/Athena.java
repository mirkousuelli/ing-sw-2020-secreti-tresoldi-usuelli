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
 *  If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn
 */

public class Athena extends Card {
    /*@class
     * it portrays the power of Athena
     */

    public Athena() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.ATHENA, Effect.OPPONENT_TURN);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*@function
         * Unused
         */
        return true;
    }

    @Override
    public boolean usePower(List<Player> opponents) {
        /*@function
         * it implements Athena's power
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