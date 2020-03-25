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
import it.polimi.ingsw.model.cards.gods.exceptions.UnusedPowerException;
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

/*Power:
 *  You also win if your Worker moves down two or more levels
 */

public class Pan extends Card {
    /*@class
     * it portrays the power of Pan
     */

    public Pan() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.PAN, Effect.WIN_CONDITION);
    }

    @Override
    public void usePower(Cell cell) throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public void usePower(List<Player> opponents) throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public boolean usePower() {
        /*@function
         * it implements Pan's power
         */

        return getOwner().getCurrentWorker().getPreviousBuild().getLevel().toInt() - getOwner().getCurrentWorker().getLocation().getLevel().toInt() >= 2;
    }
}