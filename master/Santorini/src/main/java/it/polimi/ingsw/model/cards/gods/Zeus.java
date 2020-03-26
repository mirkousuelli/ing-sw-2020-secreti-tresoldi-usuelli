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
    public void usePower(Cell cell) throws TopLevelTowerException {
        /*@function
         * It implements Zeus' power
         */

        Block temp = (Block) getOwner().getCurrentWorker().getLocation();

        if (cell == null) throw new NullPointerException("Cell is null!");

        switch (temp.getLevel()) { //TO-DO parse() in Level
            case GROUND:
                temp.setLevel(Level.BOTTOM);
                temp.setPreviousLevel(Level.GROUND);
                break;

            case BOTTOM:
                temp.setLevel(Level.MIDDLE);
                temp.setPreviousLevel(Level.BOTTOM);
                break;

            case MIDDLE:
                temp.setLevel(Level.TOP);
                temp.setPreviousLevel(Level.MIDDLE);
                break;

            case TOP:
                throw new TopLevelTowerException("Cannot build a dome!");
                
            case DOME:
                throw new TopLevelTowerException("Null!");
        }
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