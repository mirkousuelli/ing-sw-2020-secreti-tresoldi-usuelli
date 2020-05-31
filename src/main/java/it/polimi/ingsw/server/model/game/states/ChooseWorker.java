/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.ActivePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChooseWorker implements GameState {
     /* @abstractClass
     * it represents the state where a player must choose the worker he wants to move
     */

    private final Game game;

    public ChooseWorker(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    private boolean cannotMoveAny() {
        /* @predicate
         * it checks if the current player cannot move any of his workers
         */

        List<Worker> workerList = game.getCurrentPlayer().getWorkers();

        return workerList.stream().noneMatch(w -> Move.isPresentAtLeastOneCellToMoveTo(game, w.getLocation()));
    }

    @Override
    public String getName() {
        return State.CHOOSE_WORKER.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent();

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell chosenWorker = game.getBoard().getCell(cell.getX(), cell.getY());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_WORKER);

        File f = new File(Lobby.backupPath);
        if (!f.exists()) {
            try {
                boolean b = f.createNewFile();
            } catch (IOException e) {
                returnContent.setAnswerType(AnswerType.ERROR);
                returnContent.setState(State.CHOOSE_WORKER);
            }
        }
        GameMemory.save(game, Lobby.backupPath);

        //validate input
        if (chosenWorker.isFree() || !game.getCurrentPlayer().getWorkers().contains((Worker) ((Block) chosenWorker).getPawn()))
            return returnContent;


        // if currentPlayer cannot move any of his workers, the game switches to Defeat state (he loses)
        if(cannotMoveAny()) {
            if (game.getNumPlayers() > 2) {
                returnContent.setAnswerType(AnswerType.DEFEAT);
                returnContent.setState(State.CHOOSE_WORKER);
                returnContent.setPayload(new ReducedPlayer(game.getCurrentPlayer().nickName));
                returnContent.setChangeTurn(true);
            }
            else {
                returnContent.setAnswerType(AnswerType.VICTORY);
                returnContent.setState(State.VICTORY);
                returnContent.setPayload(new ReducedPlayer(game.getPlayerList().stream()
                        .filter(p -> !p.nickName.equals(game.getCurrentPlayer().nickName))
                        .reduce(null, (a, b) -> a != null ? a : b)
                        .nickName
                        )
                );
            }

            GameMemory.save(game.getPlayerList(), Lobby.backupPath);
        }
        else {
            for (Worker w : currentPlayer.getWorkers()) {
                if (w.getX() == chosenWorker.getX() && w.getY() == chosenWorker.getY()) {
                    if(Move.isPresentAtLeastOneCellToMoveTo(game, w.getLocation())) {
                        // the player has to pick a worker and the game goes to Move state
                        currentPlayer.setCurrentWorker(w);
                        returnContent.setAnswerType(AnswerType.SUCCESS);
                        returnContent.setState(State.MOVE);
                        returnContent.setPayload(ChooseWorker.preparePayloadMove(game, Timing.DEFAULT, State.CHOOSE_WORKER));

                        GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.backupPath);
                        GameMemory.save(currentPlayer, returnContent.getState(), Lobby.backupPath);
                        GameMemory.save(game, Lobby.backupPath);
                    }
                    break;
                }
            }
        }

        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }

    public static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state) {
        List<Cell> possibleMoves;
        List<Cell> possibleBuilds;
        List<Cell> specialMoves;
        List<ReducedAnswerCell> toReturnMalus;
        List<ReducedAnswerCell> toReturn;
        boolean personalMalus = false;
        boolean removed = false;

        List<Malus> maluses = game.getCurrentPlayer().getMalusList();
        if (game.getCurrentPlayer().getCard().getPower(0).getPersonalMalus() != null) {
            Malus temp = game.getCurrentPlayer().removePermanentMalus();
            maluses.removeIf(m -> m.equals(temp));
            removed = true;
        }

        if (state.equals(State.CHOOSE_WORKER) || state.equals(State.MOVE))
            possibleMoves = new ArrayList<>(game.getBoard().getPossibleMoves(game.getCurrentPlayer()));
        else
            possibleMoves = new ArrayList<>();

        if (removed)
            game.getCurrentPlayer().addMalus(game.getCurrentPlayer().getCard().getPower(0).getPersonalMalus());

        specialMoves = new ArrayList<>(game.getBoard().getSpecialMoves(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        toReturn = ReducedAnswerCell.prepareList(ReducedAction.MOVE, game.getPlayerList(), possibleMoves, specialMoves);

        //personal malus
        Power power = game.getCurrentPlayer().getCard().getPower(0);
        Malus malus = power.getPersonalMalus();
        if (state.equals(State.CHOOSE_WORKER) && malus != null && malus.getMalusType().equals(MalusType.MOVE) && power.getEffect().equals(Effect.BUILD)) {
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation()));
            toReturnMalus = ReducedAnswerCell.prepareList(ReducedAction.USEPOWER, game.getPlayerList(), possibleBuilds, new ArrayList<>());
            toReturn = ChooseWorker.mergeReducedAnswerCellList(toReturn, toReturnMalus);
            personalMalus = true;
        }

        toReturn = ChooseWorker.mergeReducedAnswerCellList(toReturn, Move.addChangedCells(game, State.CHOOSE_WORKER));

        if (maluses != null && !maluses.isEmpty() && !personalMalus) {
            for (ReducedAnswerCell c : toReturn) {
                if (!ActivePower.verifyMalus(maluses, game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getBoard().getCell(c.getX(), c.getY())))
                    c.resetAction();
            }
        }

        return ChooseWorker.removeSurroundedCells(game, toReturn);
    }

    static List<ReducedAnswerCell> mergeReducedAnswerCellList(List<ReducedAnswerCell> toReturn, List<ReducedAnswerCell> tempList) {
        boolean found;
        List<ReducedAnswerCell> ret = new ArrayList<>(toReturn);

        for (ReducedAnswerCell tc : tempList) {
            found = false;
            for (ReducedAnswerCell rc : toReturn) {
                if (rc.getX() == tc.getX() && rc.getY() == tc.getY()) {
                    found = true;
                    rc.setActionList(ChooseWorker.unionActions(rc.getActionList(), tc.getActionList()));
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
}