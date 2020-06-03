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
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Build implements GameState {
    /* @Class
     * it represents the state where a player must (or can, in case of some God powers) build a block with his worker
     */

    private final Game game;

    public Build(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    private boolean isBuildPossible(Cell chosenCell) {
        /* @predicate
         * it tells if a player picked a cell where he can actually build
         */

        List<Cell> possibleBuilds = game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation());

        for (Cell c : possibleBuilds) {
            if (c.getX() == chosenCell.getX() && c.getY() == chosenCell.getY())
                return true;
        }

        return false;
    }

    @Override
    public String getName() {
        return State.BUILD.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent;

        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToBuildUp = game.getBoard().getCell(cell.getX(), cell.getY());


        //validate input
        if (cellToBuildUp == null || cellToBuildUp.isComplete())
            returnError();

        if (game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER)) //if it is asked to use a power
            returnContent = usePower(); //then usePower
        else
            returnContent = build(); //else it must be a build (verified in Controller), so build!

        if (ChangeTurn.controlWinCondition(game)) {
            returnContent.setState(State.VICTORY);
            returnContent.setAnswerType(AnswerType.VICTORY);
        }

        if (returnContent.getAnswerType().equals(AnswerType.SUCCESS)) {
            List<ReducedAnswerCell> toReturn = PreparePayload.mergeReducedAnswerCellList(((List<ReducedAnswerCell>) returnContent.getPayload()), PreparePayload.removeBlockedWorkers(game));
            returnContent.setPayload(toReturn);
        }

        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }

    private ReturnContent returnError() {
        ReturnContent returnContent = new ReturnContent();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.BUILD);

        return returnContent;
    }

    private ReturnContent usePower() {
        ReturnContent returnContent = null;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToBuildUp = game.getBoard().getCell(cell.getX(), cell.getY());
        Power p = currentPlayer.getCard().getPower(0);

        List<ReducedAnswerCell> toReturn = new ArrayList<>();

        if (((BuildPower) p).usePower(currentPlayer, cellToBuildUp, game.getBoard().getAround(cellToBuildUp))) { //if building power is successful
            ReducedAnswerCell temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getPreviousBuild(), game.getPlayerList());
            toReturn.add(temp);

            returnContent = new ReturnContent();
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.CHOOSE_WORKER);
            returnContent.setChangeTurn(true);
            returnContent.setPayload(toReturn);

            //save
            GameMemory.save((Block) cellToBuildUp, Lobby.backupPath);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.backupPath);
        }

        if (returnContent == null)
            return returnError();

        return returnContent;
    }

    private ReturnContent build() {
        ReturnContent returnContent = null;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToBuildUp = game.getBoard().getCell(cell.getX(), cell.getY());

        List<ReducedAnswerCell> toReturn = new ArrayList<>();

        //if the player chose a possible cell, the game actually builds on it and then proceed to change the turn
        if (isBuildPossible(cellToBuildUp)) {
            game.getBoard().build(currentPlayer, cellToBuildUp);

            returnContent = new ReturnContent();

            Power p = game.getCurrentPlayer().getCard().getPower(0);
            if (p.getEffect().equals(Effect.BUILD) && p.getTiming().equals(Timing.ADDITIONAL)) //if the current player's god has an additional power
                returnContent = additionalPower(); //then evaluate if it can be used
            else {
                returnContent.setState(State.CHOOSE_WORKER); //else end his turn and start a new one
                returnContent.setChangeTurn(true);
            }

            returnContent.setAnswerType(AnswerType.SUCCESS);
            toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, ReducedAnswerCell.prepareCell(cellToBuildUp, game.getPlayerList()));

            //save
            GameMemory.save((Block) cellToBuildUp, Lobby.backupPath);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.backupPath);

            returnContent.setPayload(toReturn);
        }

        if (returnContent == null)
            return returnError();

        return returnContent;
    }

    private ReturnContent additionalPower() {
        ReturnContent returnContent = new ReturnContent();
        List<ReducedAnswerCell> payload;

        payload = PreparePayload.preparePayloadBuild(game, Timing.ADDITIONAL, State.BUILD);
        PreparePayload.mergeReducedAnswerCellList(payload, PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.BUILD));


        returnContent.setAnswerType(AnswerType.SUCCESS);

        if (payload.stream()
                .map(ReducedAnswerCell::getActionList)
                .flatMap(List::stream)
                .distinct()
                .allMatch(action -> action.equals(ReducedAction.DEFAULT)) //if there are no cell where the additional power can be used
        ) {
            returnContent.setState(State.CHOOSE_WORKER); //then go to choose worker, end the current turn and start a new one
            returnContent.setChangeTurn(true);
        }
        else
            returnContent.setState(State.ASK_ADDITIONAL_POWER); //else ask if the current player wants to use the additional power

        returnContent.setPayload(payload);

        return returnContent;
    }
}