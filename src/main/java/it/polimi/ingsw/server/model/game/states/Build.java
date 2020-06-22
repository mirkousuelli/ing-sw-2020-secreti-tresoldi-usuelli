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
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the state where a player must build a block with his worker
 */
public class Build implements GameState {

    private final Game game;

    /**
     * Constructor of the state Build
     *
     * @param game the game which the state is connected to
     */
    public Build(Game game) {
        this.game = game;
    }

    /**
     * Method that tells if a player picked a cell where he can actually build
     *
     * @param chosenCell the cell where the player wants to build
     * @return {@code true} if the player can build on the chosen cell, {@code false} otherwise
     */
    private boolean isBuildPossible(Cell chosenCell) {
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

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the player picks a cell where he wants to build and, if the cell is one where he can actually build,
     * the board is updated and the state changes accordingly
     * <p>
     * After the build, it is controlled if any win condition is verified (for example if the fifth complete tower is
     * built, then the state goes to Victory
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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


        Player victorious = ChangeTurn.controlWinCondition(game);
        if (victorious != null) {
            returnContent.setState(State.VICTORY);
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setPayload(new ReducedPlayer(victorious.nickName));
        }

        //save
        GameMemory.save(game, Lobby.BACKUP_PATH);
        GameMemory.save(game.parseState(returnContent.getState()), Lobby.BACKUP_PATH);
        GameMemory.save(game.getCurrentPlayer(), returnContent.getState(), Lobby.BACKUP_PATH);

        return returnContent;
    }

    /**
     * Method that returns an error if the player picked a cell where he cannot build and has to pick another cell
     *
     * @return returnContent, containing an answer of error and the state that remains the same
     */
    private ReturnContent returnError() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.BUILD);

        return returnContent;
    }

    /**
     * Method that allows a player to use the special power; if the action is made correctly then the current player
     * changes and the state is set to ChooseWorker, otherwise he has to pick another cell where to build
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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

            returnContent = new ReturnContent<>();
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.CHOOSE_WORKER);
            returnContent.setChangeTurn(true);
            returnContent.setPayload(toReturn);

            //save
            GameMemory.save((Block) cellToBuildUp, Lobby.BACKUP_PATH);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.BACKUP_PATH);
        }

        if (returnContent == null)
            return returnError();

        return returnContent;
    }

    /**
     * Method that actually make the build when the chosen cell is one where he can build
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent build() {
        ReturnContent returnContent = null;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToBuildUp = game.getBoard().getCell(cell.getX(), cell.getY());

        List<ReducedAnswerCell> toReturn = new ArrayList<>();

        //if the player chose a possible cell, the game actually builds on it and then proceed to change the turn
        if (isBuildPossible(cellToBuildUp)) {
            game.getBoard().build(currentPlayer, cellToBuildUp);

            returnContent = new ReturnContent<>();

            Power p = game.getCurrentPlayer().getCard().getPower(0);
            if (p.getEffect().equals(Effect.BUILD) && p.getTiming().equals(Timing.ADDITIONAL)) { //if the current player's god has an additional power
                returnContent = additionalPower(); //then evaluate if it can be used
                toReturn = (List<ReducedAnswerCell>) returnContent.getPayload();
            } else {
                returnContent.setState(State.CHOOSE_WORKER); //else end his turn and start a new one
                returnContent.setChangeTurn(true);
            }

            returnContent.setAnswerType(AnswerType.SUCCESS);
            toReturn = PreparePayload.mergeReducedAnswerCellList(toReturn, ReducedAnswerCell.prepareCell(cellToBuildUp, game.getPlayerList()));

            //save
            GameMemory.save((Block) cellToBuildUp, Lobby.BACKUP_PATH);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.BACKUP_PATH);

            returnContent.setPayload(toReturn);
        }

        if (returnContent == null)
            return returnError();

        return returnContent;
    }

    /**
     * Method that allows the player to choose if he wants to use an additional power (if there is at least one cell
     * where the additional power can be used) or automatically swaps to ChooseWorker state otherwise
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent additionalPower() {
        ReturnContent returnContent = new ReturnContent<>();
        List<ReducedAnswerCell> payload;

        payload = PreparePayload.preparePayloadBuild(game, Timing.ADDITIONAL, State.BUILD);
        payload = PreparePayload.mergeReducedAnswerCellList(payload, PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.BUILD));


        returnContent.setAnswerType(AnswerType.SUCCESS);

        if (payload.stream()
                .map(ReducedAnswerCell::getActionList)
                .flatMap(List::stream)
                .distinct()
                .allMatch(action -> action.equals(ReducedAction.DEFAULT)) //if there are no cell where the additional power can be used
        ) {
            returnContent.setState(State.CHOOSE_WORKER); //then go to choose worker, end the current turn and start a new one
            returnContent.setChangeTurn(true);
        } else
            returnContent.setState(State.ASK_ADDITIONAL_POWER); //else ask if the current player wants to use the additional power

        returnContent.setPayload(payload);

        return returnContent;
    }
}