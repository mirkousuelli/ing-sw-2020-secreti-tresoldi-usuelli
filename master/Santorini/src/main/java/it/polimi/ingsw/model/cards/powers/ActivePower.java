/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.MalusPlayer;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerPosition;
import it.polimi.ingsw.model.exceptions.cards.WrongCellException;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.exceptions.map.MapDimensionException;
import it.polimi.ingsw.model.exceptions.map.NotValidCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Worker;

import java.util.List;

public abstract class ActivePower extends Power {

    public ActivePower(Card card) {
        super(card);
    }

    private void preamble(Cell cellToUse) throws Exception {
        Worker currWorker = card.getOwner().getCurrentWorker();

        if (workerType.equals(WorkerType.DEFAULT))
            workerToUse = currWorker;
        else
            workerToUse = card.getOwner().getWorkers().stream()
                    .filter(w -> !w.equals(currWorker))
                    .reduce(null, (w1, w2) -> w1 != null ? w1 : w2);

        if (workerInitPos != WorkerPosition.DEFAULT && !workerToUse.getLocation().getLevel().equals(workerInitPos)) throw new WrongWorkerException("Wrong initial location!");

        //verify constraints
        verifyConstraints(cellToUse);

        setNumberOfActionsRemaining();
    }

    private void addPersonalMalus() {
        MalusPlayer malusPlayer;

        if (malus != null) {
            malusPlayer = new MalusPlayer(malus);
            card.getOwner().addMalus(malusPlayer);
        }
    }

    private void verifyConstraints(Cell cellToUse) throws WrongCellException, NotValidCellException, MapDimensionException {
        /*if (constraints.isSameCell()) {
            if (!cellToUse.equals(workerToUse.getPreviousLocation())) throw new WrongCellException("Not same cell!");
        }

        if (constraints.isNotSameCell()) {
            if (cellToUse.equals(workerToUse.getPreviousLocation())) throw new WrongCellException("Same cell!");
        }*/

        if (constraints.isPerimCell()) {
            if (isPerim(cellToUse))  throw new WrongCellException("Not perim cell!");
        }

        if (constraints.isNotPerimCell()) {
            if (isPerim(cellToUse))  throw new WrongCellException("Perim cell!");
        }

        if (constraints.isUnderItself()) {
            if (!workerToUse.getLocation().equals(cellToUse))  throw new WrongCellException("Not under itself!");
        }
        else {
            List<Cell> around = ((Block) workerToUse.getLocation()).getAround();
            if (!(around.contains(cellToUse))) throw new WrongCellException("Cell is not adjacent!");
        }
    }

    public void usePower(Cell cellToUse) throws Exception {
        preamble(cellToUse);

        do {
            useActivePower(cellToUse);
            numberOfActionsRemaining--;
        } while (numberOfActionsRemaining > 0);

        addPersonalMalus();
    }

    protected abstract void useActivePower(Cell cellToUse) throws Exception;

    private boolean isPerim(Cell cellToUse) {
        return (cellToUse.getX() == 0 || cellToUse.getY() == 0 || cellToUse.getX() == 4 || cellToUse.getY() == 4);
    }
}
