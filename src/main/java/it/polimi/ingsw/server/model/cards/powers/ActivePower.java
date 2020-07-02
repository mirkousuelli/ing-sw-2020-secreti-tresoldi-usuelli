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
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Abstract class that represents an active power, which can be a move or a build
 * <p>
 * Many methods of this class are developed deeper by its subclasses {@link MovePower} and {@link BuildPower}
 */
public abstract class ActivePower<S> extends Power<S> {

    protected int numberOfActionsRemaining;
    protected Worker workerToUse;

    private final Map<Effect, Function<Worker, Cell>> constraintsMap = new EnumMap<>(Effect.class);
    private static final Map<MalusLevel, BiPredicate<Cell, Cell>> malusLevelMap = new EnumMap<>(MalusLevel.class);

    /**
     * Constructor of the active power that recalls its super class.
     * <p>
     * It creates a map of constraints
     */
    public ActivePower() {
        super();

        constraintsMap.put(Effect.MOVE, Worker::getPreviousLocation);
        constraintsMap.put(Effect.BUILD, Worker::getPreviousBuild);

        malusLevelMap.put(MalusLevel.UP, (workerLocation, cellToUse) -> workerLocation.getLevel().toInt() < cellToUse.getLevel().toInt());
        malusLevelMap.put(MalusLevel.DOWN, (workerLocation, cellToUse) -> workerLocation.getLevel().toInt() > cellToUse.getLevel().toInt());
        malusLevelMap.put(MalusLevel.SAME, (workerLocation, cellToUse) -> workerLocation.getLevel().toInt().equals(cellToUse.getLevel().toInt()));
        malusLevelMap.put(MalusLevel.DEFAULT, (workerLocation, cellToUse) -> false);
    }

    public int getNumberOfActionsRemaining() {
        return numberOfActionsRemaining;
    }

    public void setNumberOfActionsRemaining() {
        numberOfActionsRemaining = constraints.getNumberOfAdditional();
    }

    /**
     * Method that controls that the constraints are verified and there's no active malus on the cell
     *
     * @param currentPlayer the current player
     * @param cellToUse     the cell where to control
     * @return {@code true} if the constraints are verified properly and the cell doesn't have any malus active,
     * {@code false} otherwise
     */
    public boolean preamble(Player currentPlayer, Cell cellToUse) {
        workerToUse = null;
        Worker currentWorker = currentPlayer.getCurrentWorker();

        if (workerType.equals(WorkerType.DEFAULT))
            workerToUse = currentWorker;
        else
            workerToUse = currentPlayer.getWorkers().stream()
                    .filter(w -> !w.equals(currentWorker))
                    .reduce(null, (w1, w2) -> w1 != null ? w1 : w2);

        if (!verifyWorkerLevel(workerToUse.getLocation())) return false;

        List<Malus> correctMalus = new ArrayList<>();
        if (effect.equals(Effect.MALUS))
            correctMalus = currentPlayer.getMalusList().stream()
                    .filter(m -> m.getMalusType().equals(((Malus) allowedAction).getMalusType()))
                    .collect(Collectors.toList());

        if (!correctMalus.isEmpty() && !ActivePower.verifyMalus(correctMalus, workerToUse.getLocation(), cellToUse))
            return false;

        return verifyConstraints(cellToUse);
    }

    /**
     * Method that adds a personal malus to the current player
     *
     * @param currentPlayer the player whom the malus is added to
     */
    private void addPersonalMalus(Player currentPlayer) {
        Malus malusPlayer;

        if (personalMalus != null) {
            malusPlayer = new Malus(personalMalus);
            currentPlayer.addMalus(malusPlayer);
        }
    }

    /**
     * Method that controls the worker initial position given its cell
     *
     * @param workerLocation the cell where the worker is located
     * @return {@code true} if the level of the worker is correct, {@code false} if not
     */
    private boolean verifyWorkerLevel(Cell workerLocation) {
        if (workerInitPos.equals(WorkerPosition.DEFAULT)) return true;

        return workerLocation.getLevel().toInt() == workerInitPos.ordinal();
    }

    /**
     * Method that checks all possible constraints and that the cell is adjacent to the worker cell
     *
     * @param cellToUse cell where to control the constraints
     * @return {@code true} if the constraints are correct and the cell is adjacent to the worker position (or under
     * if we consider Zeus), {@code false} otherwise
     */
    private boolean verifyConstraints(Cell cellToUse) {
        if (constraints.isPerimCell() && !Cell.isPerim(cellToUse)) return false;
        if (constraints.isNotPerimCell() && Cell.isPerim(cellToUse)) return false;
        if (constraints.isUnderItself() && !cellToUse.equals(workerToUse.getLocation())) return false;
        if (cellToUse.isComplete()) return false;

        if (constraints.isSameCell() && !cellToUse.equals(constraintsMap.get(effect).apply(workerToUse))) return false;
        if (constraints.isNotSameCell() && cellToUse.equals(constraintsMap.get(effect).apply(workerToUse)))
            return false;

        if (constraints.isUnderItself())
            return workerToUse.getLocation().equals(cellToUse);
        else
            return isAdjacent(cellToUse);
    }

    /**
     * Method that allows the player to use an active power: it checks that it is actually possible and then proceeds
     * to use the power, reducing the number of actions remaining by one (some God powers can be used more than once
     * during the same turn)
     *
     * @param currentPlayer the current player
     * @param cellToUse     the chosen cell
     * @param adjacency     list of cells around the worker
     * @return {@code true} if the power is used properly, {@code false} if for some reason it wasn't possible to use it
     */
    public boolean usePower(Player currentPlayer, Cell cellToUse, List<Cell> adjacency) {
        if (!preamble(currentPlayer, cellToUse)) return false;
        if (timing.equals(Timing.ADDITIONAL) && numberOfActionsRemaining == 0 && constraints.getNumberOfAdditional() >= 1)
            return false;
        if (numberOfActionsRemaining == 0 && constraints.getNumberOfAdditional() == -1) return false;
        if (!useActivePower(currentPlayer, cellToUse, adjacency)) return false;

        if (numberOfActionsRemaining > 0)
            numberOfActionsRemaining--;

        addPersonalMalus(currentPlayer);

        return true;
    }

    /**
     * Abstract method that allows the player to use an active power
     * <p>
     * It is defined inside the classes {@link MovePower} and {@link BuildPower} and it is developed by this classes
     * depending on the power type
     *
     * @param currentPlayer player that uses the power
     * @param cellToUse     cell where to use the power
     * @param adjacency     list of cells around worker's position
     * @return {@code true} if the power is used correctly, {@code false} otherwise
     */
    protected abstract boolean useActivePower(Player currentPlayer, Cell cellToUse, List<Cell> adjacency);

    /**
     * Method that tells if the given cell is adjacent to the worker position
     *
     * @param cellToUse the cell that gets checked
     * @return {@code true} if the cell is adjacent to the worker position, {@code false} otherwise
     */
    private boolean isAdjacent(Cell cellToUse) {
        return (cellToUse.getX() - workerToUse.getLocation().getX() >= -1 && cellToUse.getX() - workerToUse.getLocation().getX() <= 1 &&
                cellToUse.getY() - workerToUse.getLocation().getY() >= -1 && cellToUse.getY() - workerToUse.getLocation().getY() <= 1);
    }

    /**
     * Method that checks if a certain player during his turn has a malus on the specified cell, not allowing him to
     * do specific actions on that
     * <p>
     * The malus can depend on the direction (which can be different depending on the different Gods) that the malus
     * is applied to: for example Persephone forces opponents to move up whenever they can
     *
     * @param malus  the god's mlaus of the current player
     * @param worker the current worker
     * @return {@code true} if the chosen cell has a malus active on it, {@code false} otherwise
     */
    public static boolean verifyMalus(Malus malus, Worker worker) {
        List<Malus> maluses = new ArrayList<>();
        maluses.add(malus);

        return !verifyMalus(maluses, worker.getPreviousLocation(), worker.getLocation());
    }

    /**
     * Method that checks if a certain cell has any malus on it.
     * <p>
     * The malus can depend on the direction (which can be different depending on the different Gods) that the malus
     * is applied to: for example Persephone forces opponents to move up whenever they can
     *
     * @param maluses        list of maluses
     * @param workerLocation the location of the worker
     * @param cellToUse      the cell that gets checked
     * @return {@code false} if the chosen cell has a malus active on it, {@code true} otherwise
     */
    public static boolean verifyMalus(List<Malus> maluses, Cell workerLocation, Cell cellToUse) {
        List<Malus> malusesFiltered;
        BiPredicate<Cell, Cell> biFunct;

        if (maluses != null) {
            malusesFiltered = maluses.stream()
                    .filter(malus -> malus.isPermanent() || malus.getNumberOfTurnsUsed() < malus.getNumberOfTurns())
                    .collect(Collectors.toList());

            for (Malus malus : malusesFiltered) {
                for (MalusLevel direction : malus.getDirection()) {
                    biFunct = malusLevelMap.get(direction);
                    if (biFunct != null && biFunct.test(workerLocation, cellToUse))
                        return false;
                }
            }
        }
        return true;
    }
}