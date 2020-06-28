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
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that represents the state where a player has to choose the worker he wants to use during his turn
 */
public class ChooseWorker implements GameState {

    private final Game game;
    private static final Logger LOGGER = Logger.getLogger(ChooseWorker.class.getName());

    /**
     * Constructor of the state ChooseWorker
     *
     * @param game the game which the state is connected to
     */
    public ChooseWorker(Game game) {
        this.game = game;
    }

    /**
     * Method that tells if the given player cannot move any of his workers
     *
     * @param game   the game where to check
     * @param player the player to control
     * @return {@code true} if the given player cannot move any of his workers, {@code false} if he can move at least
     * one of them
     */
    static boolean cannotMoveAny(Game game, Player player) {
        List<Worker> workerList = player.getWorkers();

        return workerList.stream().noneMatch(worker -> ChooseWorker.isWorkerAbleToMove(game, worker));
    }

    /**
     * Method that tells if the current player cannot move any of his workers
     *
     * @param game the game that is being played
     * @return {@code true} if the current player cannot move any of his workers, {@code false} if he can move at least
     * one of them
     */
    private static boolean cannotMoveAny(Game game) {
        return ChooseWorker.cannotMoveAny(game, game.getCurrentPlayer());
    }

    /**
     * Method that checks if the given worker can be moved
     *
     * @param worker the worker that is checked
     * @return {@code true} if the chosen worker can be moved, {@code false} otherwise
     */
    private static boolean isWorkerAbleToMove(Game game, Worker worker) {
        List<Cell> around = game.getBoard().getAround(worker.getLocation());

        return around.stream()
                .filter(Cell::isWalkable)
                .anyMatch(cell -> cell.getLevel().toInt() - worker.getLocation().getLevel().toInt() <= 1);
    }

    @Override
    public String getName() {
        return State.CHOOSE_WORKER.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the current player picks the cell of the worker he wants to use during the turn: if with the chosen
     * worker has at least one cell where it can be moved, then the state is set to Move, otherwise the player must pick
     * again
     * <p>
     * If the current player cannot move any of his workers he loses and the turn is changed: if after his elimination
     * there's only one player left, then the remaining player is the winner and the turn is set to Victory state
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent;

        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell chosenWorker = game.getBoard().getCell(cell.getX(), cell.getY());

        if (!Files.exists(Paths.get(Lobby.BACKUP_PATH))) {
            try {
                Files.createFile(Paths.get(Lobby.BACKUP_PATH));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Got an exception while opening " + Lobby.BACKUP_PATH, e);
                return returnError();
            }

            //first save
            GameMemory.save(game, Lobby.BACKUP_PATH);
        }

        //validate input
        if (chosenWorker.isFree() || !game.getCurrentPlayer().getWorkers().contains(((Block) chosenWorker).getPawn()))
            return returnError();

        if (ChooseWorker.cannotMoveAny(game)) //if currentPlayer cannot move any of his workers
            returnContent = removeWorkersAndPlayer(); //then he loses and his workers have to be removed
        else //else he can choose one of his workers
            returnContent = chooseWorker(chosenWorker);

        //save
        GameMemory.save(game.parseState(returnContent.getState()), Lobby.BACKUP_PATH);
        GameMemory.save(game.getCurrentPlayer(), returnContent.getState(), Lobby.BACKUP_PATH);

        return returnContent;
    }

    /**
     * Method that returns an error if the player picked a cell where he cannot move to and has to pick another cell
     *
     * @return returnContent, containing an answer of error and the state that remains the same
     */
    private ReturnContent returnError() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHOOSE_WORKER);

        return returnContent;
    }


    /**
     * Method that removes the player (with his workers) that cannot has lost. If there are more than 2 players the
     * opponents keep playing the game, otherwise the other player is the winner
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent removeWorkersAndPlayer() {
        ReturnContent returnContent = new ReturnContent<>();
        Player currentPlayer = game.getCurrentPlayer();

        if (game.getNumPlayers() > 2) {
            returnContent.setAnswerType(AnswerType.DEFEAT);
            returnContent.setState(State.CHOOSE_WORKER);
            returnContent.setPayload(new ReducedPlayer(currentPlayer.nickName));
            returnContent.setChangeTurn(true);

            game.getCurrentPlayer().removeWorkers();
            game.removePlayer(currentPlayer.getNickName());
            game.setNumPlayers(game.getNumPlayers() - 1);

            //save
            GameMemory.save(game.getPlayerList(), Lobby.BACKUP_PATH);
        } else {
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setState(State.VICTORY);
            returnContent.setPayload(new ReducedPlayer(game.getPlayerList().stream()
                            .filter(p -> !p.nickName.equals(game.getCurrentPlayer().nickName))
                            .reduce(null, (a, b) -> a != null ? a : b)
                            .nickName
                    )
            );
        }

        return returnContent;
    }

    /**
     * Method that actually picks the player that has been selected by the player
     *
     * @param chosenWorker the cell containing the worker picked by the player
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent chooseWorker(Cell chosenWorker) {
        ReturnContent returnContent = null;
        Player currentPlayer = game.getCurrentPlayer();
        List<ReducedAnswerCell> payload = null;

        for (Worker w : currentPlayer.getWorkers()) {
            if (w.getX() == chosenWorker.getX() && w.getY() == chosenWorker.getY()) {
                if (ChooseWorker.isWorkerAbleToMove(game, w)) {
                    // the player has to pick a worker and the game goes to Move state
                    currentPlayer.setCurrentWorker(w);

                    returnContent = new ReturnContent<>();
                    returnContent.setAnswerType(AnswerType.SUCCESS);
                    returnContent.setState(State.MOVE);
                    payload = PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.CHOOSE_WORKER);

                    //save
                    GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.BACKUP_PATH);
                    GameMemory.save(currentPlayer, returnContent.getState(), Lobby.BACKUP_PATH);
                    GameMemory.save(game, Lobby.BACKUP_PATH);
                }
                break;
            }
        }

        if (returnContent == null || payload.stream().allMatch(rac -> rac.getAction(0).equals(ReducedAction.DEFAULT)))
            return returnError();

        returnContent.setPayload(payload);

        return returnContent;
    }
}