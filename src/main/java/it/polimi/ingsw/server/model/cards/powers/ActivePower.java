/*
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.List;

/**
 *
 */
public abstract class ActivePower<S> extends Power<S> {

    protected int numberOfActionsRemaining;
    protected Worker workerToUse;

    public ActivePower() {
        super();
    }

    public void setNumberOfActionsRemaining() {
        numberOfActionsRemaining = constraints.getNumberOfAdditional();
    }

    private boolean preamble(Player currentPlayer, Cell cellToUse) {
        Worker currentWorker = currentPlayer.getCurrentWorker();

        if (workerType.equals(WorkerType.DEFAULT))
            workerToUse = currentWorker;
        else
            workerToUse = currentPlayer.getWorkers().stream()
                    .filter(w -> !w.equals(currentWorker))
                    .reduce(null, (w1, w2) -> w1 != null ? w1 : w2);

        if (verifyMalus(currentPlayer, cellToUse)) return false;

        setNumberOfActionsRemaining();

        //verify constraints
        return verifyConstraints(cellToUse);
    }

    private void addPersonalMalus(Player currentPlayer) {
        Malus malusPlayer;

        if (personalMalus != null) {
            malusPlayer = new Malus(personalMalus);
            currentPlayer.addMalus(malusPlayer);
        }
    }

    private boolean verifyConstraints(Cell cellToUse) {
        if (constraints.isPerimCell() && !isPerim(cellToUse))  return false;
        if (constraints.isNotPerimCell() && isPerim(cellToUse))  return false;
        if (constraints.isUnderItself() && !cellToUse.equals(workerToUse.getLocation())) return false;
        if (cellToUse.isComplete()) return false;

        if (timing.equals(Timing.DEFAULT)) {
            //----------------------------------------------------
        }

        if (constraints.isUnderItself())
            return workerToUse.getLocation().equals(cellToUse);
        else
            return isAdjacent(cellToUse);
    }

    public boolean usePower(Player currentPlayer, Cell cellToUse, List<Cell> adjacency) {
        if(!preamble(currentPlayer, cellToUse)) return false;

        if(!useActivePower(currentPlayer, cellToUse, adjacency)) return false;

        numberOfActionsRemaining--;
        addPersonalMalus(currentPlayer);

        return true;
    }

    //Overload
    protected abstract boolean useActivePower(Player currentPlayer, Cell cellToUse, List<Cell> adjacency);

    private boolean isPerim(Cell cellToUse) {
        return (cellToUse.getX() == 0 || cellToUse.getY() == 0 || cellToUse.getX() == 4 || cellToUse.getY() == 4);
    }

    private boolean isAdjacent(Cell cellToUse) {
        return (cellToUse.getX() - workerToUse.getLocation().getX() >= -1 && cellToUse.getX() - workerToUse.getLocation().getX() <=1  &&
                cellToUse.getY() - workerToUse.getLocation().getY() >= -1 && cellToUse.getY() - workerToUse.getLocation().getY() <=1);
    }

    protected boolean verifyMalus(Player currentPlayer, Cell cellToUse) {
        if (currentPlayer.getMalusList() != null) {
            for (Malus malus : currentPlayer.getMalusList()) {
                for (MalusLevel direction : malus.getDirection()) {
                    switch (direction) {
                        case UP:
                            if (currentPlayer.getCurrentWorker().getLocation().getLevel().toInt() > cellToUse.getLevel().toInt())
                                return false;
                            break;
                        case DOWN:
                            if (currentPlayer.getCurrentWorker().getLocation().getLevel().toInt() < cellToUse.getLevel().toInt())
                                return false;
                            break;
                        case SAME:
                            if (currentPlayer.getCurrentWorker().getLocation().getLevel().toInt().equals(cellToUse.getLevel().toInt()))
                                return false;
                            break;
                        case DEFAULT:
                            return true;
                        default:
                            return false;
                    }
                }
            }
        }
        return false;
    }
}
