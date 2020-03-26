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
import it.polimi.ingsw.model.exceptions.cards.UnusedPowerException;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Level;
import it.polimi.ingsw.model.map.Worker;

import java.util.List;
import java.util.stream.Collectors;

/*Power:
 *  If your unmoved Worker is on the ground level, it may build up to three times
 */

public class Poseidon extends Card {
    /*@class
     * it portrays the power of Poseidon
     */

    public Poseidon() {
        /*@constructor
         * it calls the constructor of the superclass
         */

        super(God.POSEIDON, Effect.YOUR_TURN);
    }

    @Override
    public void usePower(Cell cell) throws Exception/*NullPointerException*/ {
        /*@function
         * it implements Poseidon's power
         */

        if (cell == null) throw new NullPointerException("cell is null!");

        List<Worker> unmovedWorkers = getOwner().getWorkers()
                                                .stream()
                                                .filter(worker -> !worker.equals(getOwner().getCurrentWorker()))
                                                .collect(Collectors.toList());

        if (!unmovedWorkers.get(0).getLocation().getLevel().equals(Level.GROUND)) throw new NullPointerException("No unmoved worker on ground level!");

        unmovedWorkers.get(0).build(cell);


        /*if (getOwner().getWorkers().get(0).equals(getOwner().getCurrentWorker())){
            if (!getOwner().getWorkers().get(1).getLocation().getLevel().equals(Level.GROUND)) throw new NullPointerException("No unmoved worker on ground level!");
            getOwner().getWorkers().get(1).build(cell);

        if (getOwner().getWorkers().get(0).equals(getOwner().getCurrentWorker())){
            if (!getOwner().getWorkers().get(1).getLocation().getLevel().equals(Level.GROUND)) throw new NullPointerException("No unmoved worker on ground level!");
            System.out.println(getOwner().getWorkers().get(1).build(cell));
            System.out.println(getOwner().getWorkers().get(1).getX() + ", " + getOwner().getWorkers().get(1).getY());
            for (Cell c: ((Block) cell).getAround()) System.out.println(c.getX() + ", " + c.getY());

        }
        else{
            if (!getOwner().getWorkers().get(1).getLocation().getLevel().equals(Level.GROUND)) throw new NullPointerException("No unmoved worker on ground level!");
            getOwner().getWorkers().get(0).build(cell);

        }*/

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