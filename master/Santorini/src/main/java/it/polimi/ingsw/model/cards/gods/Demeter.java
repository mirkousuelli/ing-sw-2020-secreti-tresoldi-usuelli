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
import it.polimi.ingsw.model.exceptions.cards.InitialCellException;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

/*Power:
 *  Your Worker may build one additional time, but not on the same space
 */

public class Demeter extends Card {
    /*@class
     * it portrays the power of Demeter
     */

    public Demeter() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.DEMETER, Effect.BUILD);
    }

    @Override
    public void usePower(Cell cell) throws Exception /*InitialSpaceException*/ {
        /*@function
         * it implements Demeter's power
         */

        if (getOwner().getCurrentWorker().getPreviousBuild().equals(cell)) throw new InitialCellException("It is the same cell!");

        getOwner().build(cell);
    }

    @Override
    public void usePower(List<Player> opponents) throws Exception {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public boolean usePower() throws Exception {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }
}