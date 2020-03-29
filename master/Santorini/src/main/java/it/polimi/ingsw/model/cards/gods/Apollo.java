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
import it.polimi.ingsw.model.exceptions.cards.EmptyCellException;
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Worker;

import java.util.List;

/*Power:
 *  Your Worker may move into an opponent Worker’s space by forcing their Worker to the space yours just vacated
 */

public class Apollo extends Card {
    /*@class
     * it portrays the power of Apollo
     */

    public Apollo() {
        /*@constructor
         * it calls the constructor of the superclass
         */

        super(God.APOLLO, Effect.MOVE);
    }

    @Override
    public void usePower(Cell opponentWorkerCell) throws Exception/*EmptyCellException, WrongWorkerException*/ {
        /*@function
         * it implements Apollo's power
         */

        Worker apollo = getOwner().getCurrentWorker();
        Worker opponentWorker = ((Worker) ((Block) opponentWorkerCell).getPawn());
        Block temp = (Block) apollo.getLocation();

        if (opponentWorkerCell == null) throw new NullPointerException("Cell is null!");
        if (opponentWorkerCell.isFree()) throw new EmptyCellException("Empty cell!");
        if (opponentWorker.getPlayer().equals(getOwner())) throw new WrongWorkerException("Not an opponent worker");

        //swap
        apollo.setPreviousLocation(apollo.getLocation());
        opponentWorker.setPreviousLocation(opponentWorkerCell);

        ((Block) apollo.getLocation()).removePawn();
        opponentWorker.setLocation(temp);
        apollo.setLocation((Block) opponentWorkerCell);

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