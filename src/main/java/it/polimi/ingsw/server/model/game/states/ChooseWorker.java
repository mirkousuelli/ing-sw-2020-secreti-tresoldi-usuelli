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

import java.util.ArrayList;
import java.util.List;

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

        return workerList.stream().allMatch(this::cannotMove);
    }

    private boolean cannotMove(Worker worker) {
        return !worker.isMovable();
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
        Cell chosenWorker = new Block(cell.getX(), cell.getY());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_WORKER);


        // if currentPlayer cannot move any of his workers, the game switches to Defeat state (he loses)
        if(cannotMoveAny()) {
            returnContent.setAnswerType(AnswerType.DEFEAT);
            int newCur = (game.getIndex(game.getCurrentPlayer()) + 1) % game.getNumPlayers();
            game.getPlayerList().remove(game.getCurrentPlayer());
            game.setCurrentPlayer(game.getPlayer(newCur));
            returnContent.setChangeTurn(true);
            //TODO
        }
        else {
            for (Worker w : currentPlayer.getWorkers()) {
                if (w.getX() == chosenWorker.getX() && w.getY() == chosenWorker.getY()) {
                    if(!cannotMove(w)) {
                        // the player has to pick a worker and the game goes to Move state
                        currentPlayer.setCurrentWorker(w);
                        returnContent.setAnswerType(AnswerType.SUCCESS);
                        returnContent.setState(State.MOVE);
                        returnContent.setPayload(ChooseWorker.preparePayloadMove(game, Timing.DEFAULT, State.CHOOSE_WORKER));
                    }
                    else {
                        // if the curPlayer cannot move with the chosen worker, he gets to choose a different one and the game goes to ChooseWorker state
                        currentPlayer.removeWorker(currentPlayer.getCurrentWorker());
                        returnContent.setPayload(new ReducedWorker(w, currentPlayer.nickName)); //TODO
                    }
                    break;
                }
            }
        }

        return returnContent;
    }

    public static List<ReducedAnswerCell> preparePayloadMove(Game game, Timing timing, State state) {
        List<Cell> possibleMoves;
        List<Cell> possibleBuilds;
        List<ReducedAnswerCell> toReturnMalus;
        List<ReducedAnswerCell> tempList = new ArrayList<>();
        ReducedAnswerCell temp;

        if (state.equals(State.CHOOSE_WORKER))
            possibleMoves = new ArrayList<>(game.getBoard().getPossibleMoves(game.getCurrentPlayer()));
        else
            possibleMoves = new ArrayList<>();
        List<Cell> specialMoves = new ArrayList<>(game.getBoard().getSpecialMoves(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        List<ReducedAnswerCell> toReturn = ReducedAnswerCell.prepareList(ReducedAction.MOVE, game.getPlayerList(), possibleMoves, specialMoves);

        Power power = game.getCurrentPlayer().getCard().getPower(0);
        Malus malus = power.getPersonalMalus();
        if (malus !=  null && malus.getMalusType().equals(MalusType.MOVE) && power.getEffect().equals(Effect.BUILD)) {
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation()));
            toReturnMalus = ReducedAnswerCell.prepareList(ReducedAction.BUILD, game.getPlayerList(), possibleBuilds, new ArrayList<>());

            toReturn = ChooseWorker.mergeReducedAnswerCellList(toReturn, toReturnMalus);

        }

        temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getPlayerList());
        tempList.add(temp);

        temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getPreviousLocation(), game.getPlayerList());
        tempList.add(temp);

        return ChooseWorker.mergeReducedAnswerCellList(toReturn, tempList);
    }

    public static List<ReducedAnswerCell> mergeReducedAnswerCellList(List<ReducedAnswerCell> toReturn, List<ReducedAnswerCell> tempList) {
        boolean found;
        for (ReducedAnswerCell tc : tempList) {
            found = false;
            for (ReducedAnswerCell rc : toReturn) {
                if (rc.getX() == tc.getX() && rc.getY() == tc.getY()) {
                    found = true;
                    break;
                }
            }

            if (!found)
                toReturn.add(tc);
            }

        return toReturn;
    }
}