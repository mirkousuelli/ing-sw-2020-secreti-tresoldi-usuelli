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
import it.polimi.ingsw.model.exceptions.cards.TopLevelTowerException;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;

import java.util.List;

/*Power:
 *Your Worker may build a block under itself
 */

public class Zeus extends Card {
    /*@class
     * it portrays the power of Zeus
     */

    public Zeus() {
        /*@constructor
         * it calls the constructor of the superclass
         */

        super(God.ZEUS, Effect.BUILD);
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
    public boolean usePower() throws Exception {
        /*@function
         * It implements Zeus' power
         */

        Block temp = (Block) getOwner().getCurrentWorker().getLocation();

        if (temp.getLevel().equals(Level.TOP)) throw new TopLevelTowerException("Cannot build a dome!");

        temp.setLevel(temp.getLevel().parseInt(temp.getLevel().toInt() + 1));

        return true;
    }
}