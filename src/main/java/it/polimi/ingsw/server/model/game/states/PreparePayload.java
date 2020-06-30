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

public class PreparePayload {

    //PreparePayload is a static class, so its constructor must be private!
    private PreparePayload() {
    }

    public static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state) {
        if (game == null || timing == null || state == null) return new ArrayList<>();

        return PreparePayload.preparePayloadMove(game, timing, state, true);
    }

    private static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state, boolean currentWorker) {
        List<ReducedAnswerCell> toReturn;
        List<ReducedAnswerCell> toReturnWithPersonalMalus;

        Player currentPlayer = game.getCurrentPlayer();
        Worker worker = currentPlayer.getCurrentWorker();
        Cell workerLocation = worker.getLocation();

        List<Malus> maluses = currentPlayer.getMalusList();
        boolean personalMalus = false;

        if (maluses.stream().anyMatch(Malus::isPermanent) &&
                game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER) &&
                currentPlayer.getCard().getPower(0).getPersonalMalus() != null)
            maluses.removeIf(Malus::isPermanent);


        toReturn = PreparePayload.preparePayloadMoveBasic(game, timing, state); //possible and special moves
        toReturnWithPersonalMalus = PreparePayload.preparePayloadMovePersonalMalus(game, state, toReturn); //remove cell blocked by a personal malus
        if (!toReturnWithPersonalMalus.isEmpty()) {
            personalMalus = true;
            toReturn = toReturnWithPersonalMalus;
        }
        toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, PreparePayload.addChangedCells(game, State.CHOOSE_WORKER)); //add changed cell by a move action

        if (!maluses.isEmpty() && !personalMalus) {
            for (ReducedAnswerCell c : toReturn) {
                if (!ActivePower.verifyMalus(maluses, workerLocation, game.getBoard().getCell(c.getX(), c.getY()))) //if a malus (personal or not) is active on a cell
                    c.resetAction(); //then remove every possible action on that cell
            }
        }

        if (currentWorker)
            toReturn = PreparePayload.preparePayloadMovePermanentMalus(game, timing, state, toReturn); //verify if a permanent "If possible" malus is active

        return PreparePayload.removeSurroundedCells(game, toReturn); //it will prevent the player to block himself
    }

    private static List<ReducedAnswerCell> preparePayloadMoveBasic(Game game, Timing timing, State state) {
        List<Cell> possibleMoves = new ArrayList<>();
        List<Cell> specialMoves;

        Player currentPlayer = game.getCurrentPlayer();

        if (state.equals(State.CHOOSE_WORKER) || state.equals(State.MOVE)) //if it is possible to move normally
            possibleMoves = new ArrayList<>(game.getBoard().getPossibleMoves(currentPlayer)); //then add the possible moves

        specialMoves = new ArrayList<>(game.getBoard().getSpecialMoves(currentPlayer.getCurrentWorker().getLocation(), currentPlayer, timing)); //add special moves

        return ReducedAnswerCell.prepareList(ReducedAction.MOVE, game.getPlayerList(), possibleMoves, specialMoves);
    }

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

    private static List<ReducedAnswerCell> preparePayloadMovePermanentMalus(Game game, Timing timing, State state, List<ReducedAnswerCell> toReturn) {
        Player currentPlayer = game.getCurrentPlayer();
        boolean isToReturnOnlyDefault = toReturn.stream()
                .allMatch(rac -> rac.getAction(0).equals(ReducedAction.DEFAULT));
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

    static List<ReducedAnswerCell> mergeReducedAnswerCellList(List<ReducedAnswerCell> toReturn, ReducedAnswerCell temp) {
        List<ReducedAnswerCell> tempList = new ArrayList<>();
        tempList.add(temp);

        return PreparePayload.mergeReducedAnswerCellList(toReturn, tempList);
    }

    private static List<ReducedAnswerCell> removeSurroundedCells(Game game, List<ReducedAnswerCell> toReturn) {
        List<ReducedAnswerCell> ret = new ArrayList<>();
        Cell c;

        if (!Move.isPresentAtLeastOneCellToMoveTo(game, game.getCurrentPlayer().getCurrentWorker().getLocation()))
            return new ArrayList<>();

        for (ReducedAnswerCell rac : toReturn) {
            c = game.getBoard().getCell(rac.getX(), rac.getY());
            if (Move.isPresentAtLeastOneCellToMoveTo(game, c))
                ret.add(rac);
        }

        return ret;
    }

    private static List<ReducedAction> unionActions(List<ReducedAction> list1, List<ReducedAction> list2) {
        Set<ReducedAction> set = new HashSet<>();

        set.addAll(list1);
        set.addAll(list2);

        if (set.stream().distinct().count() > 1 && set.contains(ReducedAction.DEFAULT))
            set.removeIf(ra -> ra.equals(ReducedAction.DEFAULT));

        return new ArrayList<>(set);
    }

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

    public static List<ReducedAnswerCell> preparePayloadBuildAdditional(Game game) {
        ReducedAnswerCell temp;
        List<ReducedAnswerCell> toReturn = new ArrayList<>();
        Player currentPlayer = game.getCurrentPlayer();
        Worker currentWorker = currentPlayer.getCurrentWorker();

        temp = ReducedAnswerCell.prepareCell(currentWorker.getPreviousBuild(), game.getPlayerList()); //add the new position of the current worker if it was moved in this turn
        toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, temp);

        return toReturn;
    }

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