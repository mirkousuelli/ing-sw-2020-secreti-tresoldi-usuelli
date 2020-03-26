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
import it.polimi.ingsw.model.exceptions.map.OccupiedCellException;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

/*Power:
 *  Your Worker may move one additional time, but not back to its initial space
 */

public class Arthemis extends Card {
    /*@class
     * it portrays the power of Arthemis
     */

    public Arthemis() {
        /*@constructor
         * it calls the constructor of the superclass
         */

        super(God.ARTHEMIS, Effect.MOVE);
    }

    @Override
    public void usePower(Cell cell) throws Exception /*InitialSpaceException, OccupiedCellException, CompleteTowerException, TooHighException, FarCellException*/ {
        /*@function
         * it implements Arthemis' power
         */

        if (cell == null) throw new NullPointerException("Cell is null!");
        if (cell.equals(getOwner().getCurrentWorker().getPreviousLocation())) throw new InitialCellException("Previous cell!");
        if (!cell.isFree()) throw new OccupiedCellException("Occupied cell!");

        getOwner().move(cell);
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