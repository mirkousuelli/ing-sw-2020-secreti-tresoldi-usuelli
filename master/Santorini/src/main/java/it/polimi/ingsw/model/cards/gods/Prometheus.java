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
import it.polimi.ingsw.model.cards.gods.exceptions.OccupiedCellException;
import it.polimi.ingsw.model.cards.gods.exceptions.UnusedPowerException;
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

/*Power:
 *  If your Worker does not move up, it may build both before and after moving
 */

public class Prometheus extends Card {
    /*@class
     * it portrays the power of Prometheus
     */

    public Prometheus() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.PROMETHEUS, Effect.MOVE);
    }

    @Override
    public void usePower(Cell cell) throws Exception /*OccupiedCellException*/ {
        /*@function
         * it implements Prometheus' power
         */

        getOwner().addCannotMoveUpMalus();
        getOwner().build(cell);
    }

    @Override
    public void usePower(List<Player> opponents) throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public boolean usePower() throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }
}
