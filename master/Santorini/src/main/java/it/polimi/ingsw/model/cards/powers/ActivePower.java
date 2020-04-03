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

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.MalusPlayer;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Cell;
import it.polimi.ingsw.model.map.Worker;

import java.util.List;

public abstract class ActivePower extends Power {

    public ActivePower(/*Card card*/) {
        super(/*card*/);
    }

    private boolean preamble(Player currentPlayer, Cell cellToUse) {
        Worker currentWorker = currentPlayer.getCurrentWorker();

        if (workerType.equals(WorkerType.DEFAULT))
            workerToUse = currentWorker;
        else
            workerToUse = currentPlayer.getWorkers().stream()
                    .filter(w -> !w.equals(currentWorker))
                    .reduce(null, (w1, w2) -> w1 != null ? w1 : w2);

        //if (!workerInitPos.equals(WorkerPosition.DEFAULT) && !workerToUse.getLocation().getLevel().equals(workerInitPos)) return false;

        setNumberOfActionsRemaining();

        //verify constraints
        return verifyConstraints(cellToUse);
    }

    private void addPersonalMalus(Player currentPlayer) {
        MalusPlayer malusPlayer;

        if (malus != null) {
            malusPlayer = new MalusPlayer(malus);
            currentPlayer.addMalus(malusPlayer);
        }
    }

    private boolean verifyConstraints(Cell cellToUse) {
        /*if (constraints.isSameCell() &&!cellToUse.equals(workerToUse.getPreviousLocation())) return false;
        if (constraints.isNotSameCell() && cellToUse.equals(workerToUse.getPreviousLocation())) return false;*/

        if (constraints.isPerimCell() && !isPerim(cellToUse))  return false;
        if (constraints.isNotPerimCell() && isPerim(cellToUse))  return false;
        if (constraints.isUnderItself() && !cellToUse.equals(workerToUse.getLocation())) return false;
        if (cellToUse.isComplete()) return false;


        if (constraints.isUnderItself()) {
            if (!workerToUse.getLocation().equals(cellToUse))  return false;
        }
        else {
            //List<Cell> around = ((Block) workerToUse.getLocation()).getAround();
            //if (!(around.contains(cellToUse))) return false;
        }

        return true;
    }

    public boolean usePower(Player currentPlayer, Cell cellToUse, List<Cell> adjacency) {
        if(!preamble(currentPlayer, cellToUse)) return false;

        if(!useActivePower(currentPlayer, cellToUse, adjacency)) return false;

        numberOfActionsRemaining--;
        addPersonalMalus(currentPlayer);

        return true;
    }

    protected abstract boolean useActivePower(Player currentPlayer, Cell cellToUse, List<Cell> adjacency);

    private boolean isPerim(Cell cellToUse) {
        return (cellToUse.getX() == 0 || cellToUse.getY() == 0 || cellToUse.getX() == 4 || cellToUse.getY() == 4);
    }
}
