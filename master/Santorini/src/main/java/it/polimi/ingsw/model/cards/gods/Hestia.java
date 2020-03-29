package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongCellException;
import it.polimi.ingsw.model.map.Cell;

import java.util.List;

/*Power
 *  Your Worker may build one additional time, but this cannot be on a perimeter space
 */

public class Hestia extends Card {

    public  Hestia() {
        super(God.HESTIA, Effect.BUILD);
    }

    @Override
    public void usePower(Cell cell) throws Exception {
        /*@function
         * it implements Hestia's power
         */

        if (cell.getX() == 0 || cell.getX() == 4 || cell.getY() == 0 || cell.getY() == 4) throw new WrongCellException("It is a perimeter cell!");

        getOwner().getCurrentWorker().build(cell);
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
