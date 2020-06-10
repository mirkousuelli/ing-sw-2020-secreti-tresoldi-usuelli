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
    private PreparePayload() {}

    public static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state) {
        List<Cell> possibleMoves;
        List<Cell> possibleBuilds;
        List<Cell> specialMoves;
        List<ReducedAnswerCell> toReturnMalus;
        List<ReducedAnswerCell> toReturn;
        boolean personalMalus = false;

        List<Malus> maluses = game.getCurrentPlayer().getMalusList();

        if (state.equals(State.CHOOSE_WORKER) || state.equals(State.MOVE)) //if it is possible to move normally
            possibleMoves = new ArrayList<>(game.getBoard().getPossibleMoves(game.getCurrentPlayer())); //then add the possible moves
        else
            possibleMoves = new ArrayList<>();

        specialMoves = new ArrayList<>(game.getBoard().getSpecialMoves(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing)); //add special moves
        toReturn = ReducedAnswerCell.prepareList(ReducedAction.MOVE, game.getPlayerList(), possibleMoves, specialMoves);

        //personal malus
        Power power = game.getCurrentPlayer().getCard().getPower(0);
        Malus malus = power.getPersonalMalus();
        if (state.equals(State.CHOOSE_WORKER) && malus != null && malus.getMalusType().equals(MalusType.MOVE) && power.getEffect().equals(Effect.BUILD)) { //if the current player has a personal move malus active
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation())); //then find the cells blocked by the said malus
            toReturnMalus = ReducedAnswerCell.prepareList(ReducedAction.USEPOWER, game.getPlayerList(), possibleBuilds, new ArrayList<>());
            toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, toReturnMalus);
            personalMalus = true;
        }

        toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, PreparePayload.addChangedCells(game, State.CHOOSE_WORKER)); //merge

        if (maluses != null && !maluses.isEmpty() && !personalMalus) {
            for (ReducedAnswerCell c : toReturn) {
                if (!ActivePower.verifyMalus(maluses, game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getBoard().getCell(c.getX(), c.getY()))) //if a malus (personal or not) is active on a cell
                    c.resetAction(); //then remove every possible action on that cell
            }
        }

        return PreparePayload.removeSurroundedCells(game, toReturn); //it will prevent the player to block himself
    }

    static List<ReducedAnswerCell> mergeReducedAnswerCellList(List<ReducedAnswerCell> toReturn, List<ReducedAnswerCell> tempList) {
        boolean found;
        List<ReducedAnswerCell> ret = new ArrayList<>(toReturn);

        for (ReducedAnswerCell tc : tempList) {
            found = false;
            for (ReducedAnswerCell rc : toReturn) {
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

        return mergeReducedAnswerCellList(toReturn, tempList);
    }

    private static List<ReducedAnswerCell> removeSurroundedCells(Game game, List<ReducedAnswerCell> toReturn) {
        List<ReducedAnswerCell> ret = new ArrayList<>();
        Cell c;

        if (!Move.isPresentAtLeastOneCellToMoveTo(game, game.getCurrentPlayer().getCurrentWorker().getLocation())) return new ArrayList<>();

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

        if (set.stream().distinct().count() > 1 && set.contains(ReducedAction.DEFAULT)) {
            set.removeIf(ra -> ra.equals(ReducedAction.DEFAULT));
        }

        return new ArrayList<>(set);
    }


    public static List<ReducedAnswerCell> preparePayloadBuild(Game game, Timing timing, State state) {
        List<Cell> possibleBuilds;
        List<ReducedAnswerCell> tempList = new ArrayList<>();

        if (state.equals(State.MOVE)) {
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker()));
            tempList = addChangedCells(game, State.MOVE);
        }
        else
            possibleBuilds = new ArrayList<>();

        List<Cell> specialBuilds = new ArrayList<>(game.getBoard().getSpecialBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        List<ReducedAnswerCell> toReturn = ReducedAnswerCell.prepareList(ReducedAction.BUILD, game.getPlayerList(), possibleBuilds, specialBuilds);

        return PreparePayload.mergeReducedAnswerCellList(toReturn, tempList);
    }

    static List<ReducedAnswerCell> addChangedCells(Game game, State state) {
        ReducedAnswerCell temp;
        List<ReducedAnswerCell> tempList = new ArrayList<>();
        Player currentPlayer = game.getCurrentPlayer();

        temp = ReducedAnswerCell.prepareCell(currentPlayer.getCurrentWorker().getLocation(), game.getPlayerList());
        tempList.add(temp);

        Power power = currentPlayer.getCard().getPower(0);
        if (power.getEffect().equals(Effect.MOVE) && power.getAllowedAction().equals(MovementType.PUSH) &&
                game.getState().getName().equals(State.MOVE.toString()) && game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER)) {
            Worker worker = game.getPlayerList().stream()
                    .filter(p -> !p.nickName.equals(game.getCurrentPlayer().nickName))
                    .map(Player::getWorkers)
                    .flatMap(List::stream)
                    .filter(w -> w.getPreviousLocation().equals(currentPlayer.getCurrentWorker().getLocation()))
                    .reduce(null, (a, b) -> a != null ? a : b);

            temp = ReducedAnswerCell.prepareCell(worker.getLocation(), game.getPlayerList());
            tempList.add(temp);
        }

        if (!currentPlayer.getCurrentWorker().getPreviousLocation().equals(currentPlayer.getCurrentWorker().getLocation())) {
            temp = ReducedAnswerCell.prepareCell(currentPlayer.getCurrentWorker().getPreviousLocation(), game.getPlayerList());
            if (state.equals(State.MOVE) && temp.isFree())
                temp.replaceDefaultAction(ReducedAction.BUILD);
            tempList.add(temp);
        }

        return tempList;
    }


    static List<ReducedAnswerCell> removeBlockedWorkers(Game game) {
        List<Player> playerList = game.getPlayerList();
        List<Worker> workerList = playerList.stream()
                .map(Player::getWorkers)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        List<ReducedAnswerCell> toReturn = new ArrayList<>();
        List<Cell> around;
        boolean toRemove = true;
        for (Worker w : workerList) {
            around = game.getBoard().getAround(w.getLocation());
            for (Cell c : around) {
                if (c.isWalkable())
                    toRemove = false;
            }

            if (toRemove) {
                toReturn.add(ReducedAnswerCell.prepareCell(w.getLocation(), playerList));
                PreparePayload.removeWorkerFromGame(game, w);
            }
        }

        return toReturn;
    }

    private static void removeWorkerFromGame(Game game, Worker w) {
        for (Player p : game.getPlayerList()) {
            if (p.getWorkers().contains(w)) {
                p.removeWorker(w);
                return;
            }
        }
    }
}
