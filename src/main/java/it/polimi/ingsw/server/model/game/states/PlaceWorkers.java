package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedWorker;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the state where each player has to place his workers on the board at the beginning of the game
 */
public class PlaceWorkers implements GameState {

    private final Game game;

    /**
     * Constructor of the state PlaceWorkers
     *
     * @param game the game which the state is connected to
     */
    public PlaceWorkers(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.PLACE_WORKERS.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here, starting from the starter, each player picks the cells where he wants to place his workers,
     * initialising both workers to the chosen cells and adding the pawns on them
     * <p>
     * When all the players in the game have placed their workers, the state is set to ChooseWorker
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        //chooseDeck
        ReturnContent returnContent = new ReturnContent<>();

        Player currentPlayer = game.getCurrentPlayer();
        List<ReducedWorker> workersLocations = ((List<ReducedWorker>) game.getRequest().getDemand().getPayload());
        List<ReducedAnswerCell> modifiedCell = new ArrayList<>();
        ReducedAnswerCell temp;
        Block chosenCell;
        int id;
        int nextPlayer;

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.PLACE_WORKERS);

        //safety check
        if (workersLocations.size() != 2) return returnContent;
        if (workersLocations.get(0).getX() == workersLocations.get(1).getX() && workersLocations.get(0).getY() == workersLocations.get(1).getY())
            return returnContent;


        for (ReducedWorker c : workersLocations) {
            chosenCell = (Block) game.getBoard().getCell(c.getX(), c.getY());
            if (!chosenCell.isFree())
                return returnContent;
        }

        for (ReducedWorker c : workersLocations) {
            chosenCell = (Block) game.getBoard().getCell(c.getX(), c.getY());
            id = currentPlayer.getWorkers().size() + 1;

            currentPlayer.initializeWorkerPosition(id, chosenCell);
            chosenCell.addPawn(currentPlayer.getWorker(id));

            temp = new ReducedAnswerCell(chosenCell.getX(), chosenCell.getY());
            temp.setWorker(new ReducedWorker(currentPlayer.getWorker(id), currentPlayer));
            temp.getWorker().setGender(currentPlayer.getWorkers().size() != 1);
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
