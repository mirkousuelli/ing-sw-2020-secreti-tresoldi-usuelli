package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;

import java.util.ArrayList;
import java.util.List;

public class PlaceWorkers implements GameState{

    private final Game game;

    public PlaceWorkers(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    @Override
    public String getName() {
        return "placeWorkers";
    }

    @Override
    public ReturnContent gameEngine() {
        //chooseDeck
        ReturnContent returnContent = new ReturnContent();

        //set challenger
        Player currentPlayer = game.getCurrentPlayer();
        game.setCurrentPlayer(currentPlayer);
        List<ReducedDemandCell> workersLocations = ((List<ReducedDemandCell>) game.getRequest().getDemand().getPayload());
        Block choosenCell;
        int id;
        List<Cell> modifiedCell = new ArrayList<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.PLACE_WORKERS);


        for (ReducedDemandCell c : workersLocations) {
            choosenCell = (Block) game.getBoard().getCell(c.getX(), c.getY());
            if (!choosenCell.isFree())
                return returnContent;
        }

        for (ReducedDemandCell c : workersLocations) {
            choosenCell = (Block) game.getBoard().getCell(c.getX(), c.getY());
            id = workersLocations.indexOf(c);

            currentPlayer.initializeWorkerPosition(id, choosenCell);
            choosenCell.addPawn(currentPlayer.getWorker(id));

            modifiedCell.add(choosenCell);
        }

        returnContent.setAnswerType(AnswerType.SUCCESS);
        returnContent.setState(State.CHANGE_TURN);
        returnContent.setPayload(modifiedCell);


        return returnContent;
    }
}
