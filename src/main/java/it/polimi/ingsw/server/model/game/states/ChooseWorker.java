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
                        returnContent.setPayload(PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.CHOOSE_WORKER));

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
}