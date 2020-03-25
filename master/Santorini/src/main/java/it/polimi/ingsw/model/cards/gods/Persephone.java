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
 *  If possible, at least one Worker must move up this turn
 */

public class Persephone extends Card {
    /*@class
     * it portrays the power of Persephone
     */

    public Persephone() {
        /*@constructor
         * it calls the constructor of the superclass
         */
        super(God.PERSEPHONE, Effect.OPPONENT_TURN);
    }

    @Override
    public void usePower(Cell cell) throws UnusedPowerException {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }

    @Override
    public void usePower(List<Player> opponents) throws Exception/*NullPointerException*/ {
        /*@function
         * it implements Persephone's power
         */

        if (opponents == null) throw new NullPointerException("Opponents is null");

        for (Player p : opponents) p.addMustMoveUpMalus();
    }

    @Override
    public boolean usePower() throws UnusedPowerException  {
        /*@function
         * Unused
         */

        throw new UnusedPowerException("Wrong power!");
    }
}