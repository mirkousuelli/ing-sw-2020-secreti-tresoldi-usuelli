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
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.*;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Class that represents the state where a player has to choose the worker he wants to use during his turn
 */
public class ChooseWorker implements GameState {

    private final Game game;

    /**
     * Constructor of the state ChooseWorker
     *
     * @param game the game which the state is connected to
     */
    public ChooseWorker(Game game) {
        this.game = game;
    }

    /**
     * Method that tells if the current player cannot move any of his workers
     *
     * @return {@code true} if the current player cannot move any of his workers, {@code false} if he can move at least
     * one of them
     */
    private boolean cannotMoveAny() {
        List<Worker> workerList = game.getCurrentPlayer().getWorkers();

        return workerList.stream().noneMatch(w -> Move.isPresentAtLeastOneCellToMoveTo(game, w.getLocation()));
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
        ReturnContent returnContent = new ReturnContent<>();

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell chosenWorker = game.getBoard().getCell(cell.getX(), cell.getY());

        System.out.println("1) " + currentPlayer.getMalusList().size());

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

        //save
        GameMemory.save(game, Lobby.backupPath);

        //validate input
        if (chosenWorker.isFree() || !game.getCurrentPlayer().getWorkers().contains((Worker) ((Block) chosenWorker).getPawn()))
            return returnContent;


        // if currentPlayer cannot move any of his workers, the game switches to Defeat state (he loses)
        if(cannotMoveAny()) {
            if (game.getNumPlayers() > 2) {
                returnContent.setAnswerType(AnswerType.DEFEAT);
                returnContent.setState(State.CHOOSE_WORKER);
                returnContent.setPayload(new ReducedPlayer(currentPlayer.nickName));

                int newCurrentPlayerIndex = (game.getIndex(currentPlayer) - 1) & game.getNumPlayers();
                String newCurrentPlayer = game.getPlayer(newCurrentPlayerIndex).getNickName();
                game.removePlayer(currentPlayer.getNickName());
                game.setCurrentPlayer(game.getPlayer(newCurrentPlayer));
                game.setNumPlayers(game.getNumPlayers() - 1);

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

            //save
            GameMemory.save(game.getPlayerList(), Lobby.backupPath);
        }
        else {
            System.out.println("2) " + currentPlayer.getMalusList().size());
            for (Worker w : currentPlayer.getWorkers()) {
                if (w.getX() == chosenWorker.getX() && w.getY() == chosenWorker.getY()) {
                    if(Move.isPresentAtLeastOneCellToMoveTo(game, w.getLocation())) {
                        System.out.println("4) " + currentPlayer.getMalusList().size());
                        // the player has to pick a worker and the game goes to Move state
                        currentPlayer.setCurrentWorker(w);
                        returnContent.setAnswerType(AnswerType.SUCCESS);
                        returnContent.setState(State.MOVE);
                        returnContent.setPayload(PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.CHOOSE_WORKER));

                        System.out.println("5) " + currentPlayer.getMalusList().size());

                        //save
                        GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.backupPath);
                        GameMemory.save(currentPlayer, returnContent.getState(), Lobby.backupPath);
                        GameMemory.save(game, Lobby.backupPath);
                    }
                    break;
                }
            }
        }

        System.out.println("3) " + currentPlayer.getMalusList().size());

        //save
        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }
}