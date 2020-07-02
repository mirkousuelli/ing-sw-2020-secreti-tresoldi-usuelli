package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.ActivePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.MovementType;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class containing only static methods to prepare a list of cells where a certain action can be performed
 */
public class PreparePayload {

    /**
     * Private constructor that must not be called
     */
    private PreparePayload() throws UnsupportedOperationException {
        //PreparePayload is a static class, so its constructor must be private!
        throw new UnsupportedOperationException("PreparePayload's constructor must not be called!");
    }

    /**
     * Prepares the cells the current worker can move to according to the current player's god, maluses and action performed
     *
     * @param game   the current game
     * @param state  the current state
     * @param timing the current player power's timing
     * @return the list of the cells the current player can move to
     */
    public static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state) {
        if (game == null || timing == null || state == null) return new ArrayList<>();

        return PreparePayload.preparePayloadMove(game, timing, state, true);
    }

    /**
     * Prepares the cells the current worker can move to
     *
     * @param game          the current game
     * @param state         the current state
     * @param timing        the current player power's timing
     * @param currentWorker information if the worker is the current one
     * @return the list of the cells the current player can move to
     */
    private static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state, boolean currentWorker) {
        List<ReducedAnswerCell> toReturn;
        List<ReducedAnswerCell> toReturnWithPersonalMalus;

        Player currentPlayer = game.getCurrentPlayer();
        Worker worker = currentPlayer.getCurrentWorker();
        Cell workerLocation = worker.getLocation();

        List<Malus> maluses = currentPlayer.getMalusList();

        toReturn = PreparePayload.preparePayloadMoveBasic(game, timing, state); //possible and special moves
        toReturnWithPersonalMalus = PreparePayload.preparePayloadMovePersonalMalus(game, state, toReturn); //remove cell blocked by a personal malus
        if (!toReturnWithPersonalMalus.isEmpty())
            toReturn = toReturnWithPersonalMalus;

        toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, PreparePayload.addChangedCells(game, State.CHOOSE_WORKER)); //add changed cell by a move action

        if (!maluses.isEmpty() && (currentPlayer.getCard().getPower(0).getPersonalMalus() == null || (game.getState().getName().equals("move") && maluses.contains(currentPlayer.getCard().getPower(0).getPersonalMalus())))) {
            for (ReducedAnswerCell c : toReturn) {
                if (!ActivePower.verifyMalus(maluses, workerLocation, game.getBoard().getCell(c.getX(), c.getY()))) //if a malus (personal or not) is active on a cell
                    c.resetAction(); //then remove every possible action on that cell
            }
        }

        if (currentWorker)
            toReturn = PreparePayload.preparePayloadMovePermanentMalus(game, timing, state, toReturn); //verify if a permanent "If possible" malus is active

        return PreparePayload.removeSurroundedCells(game, toReturn); //it will prevent the player to block himself
    }

    /**
     * Prepares the cells the current worker can move to with a normal move action
     *
     * @param game   the current game
     * @param state  the current state
     * @param timing the current player power's timing
     * @return the list of the cells the current player can move to
     */
    private static List<ReducedAnswerCell> preparePayloadMoveBasic(Game game, Timing timing, State state) {
        List<Cell> possibleMoves = new ArrayList<>();
        List<Cell> specialMoves;

        Player currentPlayer = game.getCurrentPlayer();

        if (state.equals(State.CHOOSE_WORKER) || state.equals(State.MOVE)) //if it is possible to move normally
            possibleMoves = new ArrayList<>(game.getBoard().getPossibleMoves(currentPlayer)); //then add the possible moves

        specialMoves = new ArrayList<>(game.getBoard().getSpecialMoves(currentPlayer.getCurrentWorker().getLocation(), currentPlayer, timing)); //add special moves

        return ReducedAnswerCell.prepareList(ReducedAction.MOVE, game.getPlayerList(), possibleMoves, specialMoves);
    }

    /**
     * Prepares the cells the current worker can move to according to the current player's personal malus
     *
     * @param game  the current game
     * @param state the current state
     * @return the list of the cells the current player can move to
     */
    private static List<ReducedAnswerCell> preparePayloadMovePersonalMalus(Game game, State state, List<ReducedAnswerCell> toReturn) {
        List<Cell> possibleBuilds;
        List<ReducedAnswerCell> toReturnMalus;

        Player currentPlayer = game.getCurrentPlayer();

        //personal malus
        Power power = currentPlayer.getCard().getPower(0);
        Malus malus = power.getPersonalMalus();
        if (state.equals(State.CHOOSE_WORKER) && malus != null && malus.getMalusType().equals(MalusType.MOVE) && power.getEffect().equals(Effect.BUILD)) { //if the current player has a personal move malus active
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(currentPlayer.getCurrentWorker().getLocation())); //then find the cells which activate the malus
            toReturnMalus = ReducedAnswerCell.prepareList(ReducedAction.USEPOWER, game.getPlayerList(), possibleBuilds, new ArrayList<>());
            return PreparePayload.mergeReducedAnswerCellList(toReturn, toReturnMalus);
        }

        return new ArrayList<>();
    }

    /**
     * Prepares the cells the current worker can move to according to the current player's permanent maluses
     *
     * @param game   the current game
     * @param state  the current state
     * @param timing the current player power's timing
     * @return the list of the cells the current player can move to
     */
    private static List<ReducedAnswerCell> preparePayloadMovePermanentMalus(Game game, Timing timing, State state, List<ReducedAnswerCell> toReturn) {
        Player currentPlayer = game.getCurrentPlayer();

        boolean isToReturnOnlyDefault = toReturn.stream()
                .allMatch(rac -> rac.getAction(0).equals(ReducedAction.DEFAULT));

        if (state.equals(State.CHOOSE_WORKER) && toReturn.stream().noneMatch(rac -> rac.getActionList().contains(ReducedAction.MOVE)))
            isToReturnOnlyDefault = true;

        List<Malus> permanentMoveMaluses = currentPlayer.getMalusList().stream()
                .filter(m -> m.getMalusType().equals(MalusType.MOVE))
                .filter(Malus::isPermanent)
                .collect(Collectors.toList());

        //permanent "If possible" maluses
        if (isToReturnOnlyDefault && !permanentMoveMaluses.isEmpty()) {
            if (state.equals(State.CHOOSE_WORKER)) {
                currentPlayer.setCurrentWorker(currentPlayer.getWorker((currentPlayer.getCurrentWorker().getId() % 2) + 1));
                List<ReducedAnswerCell> reducedAnswerCellList = PreparePayload.preparePayloadMove(game, timing, State.MOVE, false); //evaluate if the other worker can move without them
                currentPlayer.setCurrentWorker(currentPlayer.getWorker((currentPlayer.getCurrentWorker().getId() % 2) + 1));
                boolean changeWorker = reducedAnswerCellList.stream().anyMatch(rac -> !rac.getAction(0).equals(ReducedAction.DEFAULT));
                if (changeWorker) //if so
                    return new ArrayList<>(); //the current player has to choose the other worker
            }

            permanentMoveMaluses.forEach(m -> currentPlayer.removePermanentMalus()); //remove "If possible maluses"
            toReturn = PreparePayload.preparePayloadMove(game, timing, state, true); //evaluate if the current worker can move without them
            permanentMoveMaluses.forEach(currentPlayer::addMalus); //re-insert permanent "If possible" maluses

            if (!toReturn.isEmpty()) //if so
                return toReturn; //then don't apply them because it isn't possible to
        }

        return toReturn;
    }

    /**
     * Method that merges (the reduced version of) the cells contained in the two lists
     *
     * @param toReturn the first list of cells
     * @param tempList the second list of cells
     * @return a list of cells which is the outcome of the merge
     */
    public static List<ReducedAnswerCell> mergeReducedAnswerCellList(List<ReducedAnswerCell> toReturn, List<ReducedAnswerCell> tempList) {
        boolean found;
        List<ReducedAnswerCell> ret = new ArrayList<>(toReturn);

        for (ReducedAnswerCell tc : tempList) {
            found = false;
            for (ReducedAnswerCell rc : ret) {
                if (rc.getX() == tc.getX() && rc.getY() == tc.getY()) {
                    found = true;
                    rc.setActionList(PreparePayload.unionActions(rc.getActionList(), tc.getActionList()));
                    break;
                }
            }

            if (!found)
                ret.add(tc);
        }

        return ret;
    }

    /**
     * Method that adds the cell passed as parameter to the list of cells
     *
     * @param toReturn the list of cells
     * @param temp     the cell to add to the list
     * @return a list of cells which is the outcome of the merge: it contains the list of cells with the addition of
     * the chosen cell
     */
    static List<ReducedAnswerCell> mergeReducedAnswerCellList(List<ReducedAnswerCell> toReturn, ReducedAnswerCell temp) {
        List<ReducedAnswerCell> tempList = new ArrayList<>();
        tempList.add(temp);

        return PreparePayload.mergeReducedAnswerCellList(toReturn, tempList);
    }

    /**
     * Method that removes the surrounded cells from the list of cells passed as parameter
     *
     * @param game     the current game
     * @param toReturn the list of cells where to remove surrounded ones
     * @return the updated list of cells
     */
    private static List<ReducedAnswerCell> removeSurroundedCells(Game game, List<ReducedAnswerCell> toReturn) {
        List<ReducedAnswerCell> ret = new ArrayList<>();
        Cell c;

        for (ReducedAnswerCell rac : toReturn) {
            c = game.getBoard().getCell(rac.getX(), rac.getY());
            if (rac.getActionList().contains(ReducedAction.USEPOWER) || rac.getActionList().contains(ReducedAction.DEFAULT) || Move.isPresentAtLeastOneCellToMoveTo(game, c))
                ret.add(rac);
        }

        List<ReducedAction> reducedActions = ret.stream().map(ReducedAnswerCell::getActionList).flatMap(List::stream).distinct().collect(Collectors.toList());
        if (reducedActions.contains(ReducedAction.USEPOWER) && !reducedActions.contains(ReducedAction.MOVE) && game.getCurrentPlayer().getCard().getPower(0).getPersonalMalus() != null)
            return new ArrayList<>();

        return ret;
    }

    /**
     * Method that unites two lists of actions, returning a single list containing all the elements of both
     *
     * @param list1 the first list of actions
     * @param list2 the second list of actions
     * @return the updated list containing every element of both lists
     */
    private static List<ReducedAction> unionActions(List<ReducedAction> list1, List<ReducedAction> list2) {
        Set<ReducedAction> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        if (set.stream().distinct().count() > 1 && set.contains(ReducedAction.DEFAULT))
            set.removeIf(ra -> ra.equals(ReducedAction.DEFAULT));

        return new ArrayList<>(set);
    }

    /**
     * Prepares the cells the current worker can build on according to the current player's god, maluses and action performed
     *
     * @param game   the current game
     * @param state  the current state
     * @param timing the current player power's timing
     * @return the list of the cells the current player can build on
     */
    public static List<ReducedAnswerCell> preparePayloadBuild(Game game, Timing timing, State state) {
        List<Cell> possibleBuilds;
        List<ReducedAnswerCell> tempList = new ArrayList<>();

        if (state.equals(State.MOVE)) {
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker()));
            tempList = PreparePayload.addChangedCells(game, State.MOVE);
        } else
            possibleBuilds = new ArrayList<>();

        List<Cell> specialBuilds = new ArrayList<>(game.getBoard().getSpecialBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        List<ReducedAnswerCell> toReturn = ReducedAnswerCell.prepareList(ReducedAction.BUILD, game.getPlayerList(), possibleBuilds, specialBuilds);

        return PreparePayload.mergeReducedAnswerCellList(toReturn, tempList);
    }

    /**
     * Prepares the cells the current worker can build on according to the current player's god additional power
     *
     * @param game the current game
     * @return the list of the cells the current player can build on with an additional power
     */
    public static List<ReducedAnswerCell> preparePayloadBuildAdditional(Game game) {
        ReducedAnswerCell temp;
        List<ReducedAnswerCell> toReturn = new ArrayList<>();
        Player currentPlayer = game.getCurrentPlayer();
        Worker currentWorker = currentPlayer.getCurrentWorker();

        temp = ReducedAnswerCell.prepareCell(currentWorker.getPreviousBuild(), game.getPlayerList()); //add the new position of the current worker if it was moved in this turn
        toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, temp);

        return toReturn;
    }

    /**
     * Method that adds changed cells to the game
     *
     * @param game  the current game
     * @param state the current state
     * @return list of cells where there is a possible action
     */
    static List<ReducedAnswerCell> addChangedCells(Game game, State state) {
        ReducedAnswerCell temp;
        List<ReducedAnswerCell> tempList = new ArrayList<>();
        Player currentPlayer = game.getCurrentPlayer();
        Worker currentWorker = currentPlayer.getCurrentWorker();

        temp = ReducedAnswerCell.prepareCell(currentWorker.getLocation(), game.getPlayerList()); //add the new position of the current worker if it was moved in this turn
        tempList.add(temp);

        Power power = currentPlayer.getCard().getPower(0);
        if (power.getEffect().equals(Effect.MOVE) && power.getAllowedAction().equals(MovementType.PUSH) &&
                game.getState().getName().equals(State.MOVE.toString()) &&
                (game.getRequest() == null || game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER))) { //if Minotaur's power has been used during this turn
            Worker worker = game.getPlayerList().stream() //then add the new position of the opponent worker pushed by Minotaur itself
                    .filter(p -> !p.nickName.equals(currentPlayer.nickName))
                    .map(Player::getWorkers)
                    .flatMap(List::stream) //get all opponent's workers
                    .filter(w -> w.getPreviousLocation().equals(currentWorker.getLocation())) //find the opponent's worker pushed by Minotaur (its prevLoc is now Minotaur's currLoc)
                    .reduce(null, (a, b) -> a != null ? a : b);

            if (worker != null) {
                temp = ReducedAnswerCell.prepareCell(worker.getLocation(), game.getPlayerList());
                tempList.add(temp);
            }
        }

        if (!currentWorker.getPreviousLocation().equals(currentWorker.getLocation())) {
            temp = ReducedAnswerCell.prepareCell(currentWorker.getPreviousLocation(), game.getPlayerList());
            if (state.equals(State.MOVE) && temp.isFree())
                temp.replaceDefaultAction(ReducedAction.BUILD);
            tempList.add(temp);
        }

        return tempList;
    }
}