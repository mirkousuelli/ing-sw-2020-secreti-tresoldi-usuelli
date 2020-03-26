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
import it.polimi.ingsw.model.exceptions.cards.NotPerimCellException;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
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
    public void usePower(Cell cell) throws Exception /*NullPointerException, NotPerimCellException*/ {
        /*@function
         * it implements Triton's power
         */

        if (cell == null) throw new NullPointerException("cell is null!");
        if (!isPerim(cell)) throw new NotPerimCellException("cell is not perim!");

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

    public boolean isPerim(Cell cell) {
        return cell.getX() == 0 || cell.getY() == 0 || cell.getX() == 4 ||cell.getY() == 4;
    }
}