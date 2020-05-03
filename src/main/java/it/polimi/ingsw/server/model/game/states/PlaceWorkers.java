package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
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

        Player currentPlayer = game.getCurrentPlayer();
        List<ReducedDemandCell> workersLocations = ((List<ReducedDemandCell>) game.getRequest().getDemand().getPayload());
        List<ReducedAnswerCell> modifiedCell = new ArrayList<>();
        ReducedAnswerCell temp;
        Block chosenCell;
        int id;
        int nextPlayer;

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.PLACE_WORKERS);


        for (ReducedDemandCell c : workersLocations) {
            chosenCell = (Block) game.getBoard().getCell(c.getX(), c.getY());
            if (!chosenCell.isFree())
                return returnContent;
        }

        for (ReducedDemandCell c : workersLocations) {
            chosenCell = (Block) game.getBoard().getCell(c.getX(), c.getY());
            id = currentPlayer.getWorkers().size() + 1;

            System.out.println(id);
            System.out.println(currentPlayer.initializeWorkerPosition(id, chosenCell));
            chosenCell.addPawn(currentPlayer.getWorker(id));

            temp = new ReducedAnswerCell(chosenCell.getX(), chosenCell.getY());
            temp.setWorker(new ReducedWorker(currentPlayer.getWorker(id), currentPlayer.nickName));
            modifiedCell.add(temp);
        }

        returnContent.setAnswerType(AnswerType.SUCCESS);
        nextPlayer = (game.getIndex(game.getCurrentPlayer()) + 1) % game.getNumPlayers();
        if (!game.getPlayer(nextPlayer).getWorkers().isEmpty())
            returnContent.setState(State.CHOOSE_WORKER);
        returnContent.setChangeTurn(true);
        returnContent.setPayload(modifiedCell);


        return returnContent;
    }
}
