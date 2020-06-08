package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

public class AdditionalPower implements GameState {

    private final Game game;

    public AdditionalPower(Game game) {
        this.game = game;
    }

    @Override
    public String getName() {
        return State.ADDITIONAL_POWER.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent;

        ReducedDemandCell response = (ReducedDemandCell) game.getRequest().getDemand().getPayload();
        Cell c = game.getBoard().getCell(response.getX(), response.getY());
        State prevState = game.getPrevState();

        //validate input
        if (c == null)
            return returnError();


        if (prevState.equals(State.MOVE)) //if it's an additional move power
            returnContent = movePower(); //then
        else if (prevState.equals(State.BUILD)) //else if it's an additional move power
            returnContent = buildPower(); //then
        else //else report error
            returnContent = returnError();

        //save
        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }

    private ReturnContent returnError() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.ADDITIONAL_POWER);

        return returnContent;
    }

    private ReturnContent movePower() {
        ReturnContent returnContent = null;

        ReducedDemandCell response = (ReducedDemandCell) game.getRequest().getDemand().getPayload();
        Cell c = game.getBoard().getCell(response.getX(), response.getY());
        Power p = game.getCurrentPlayer().getCard().getPower(0);

        if (!Move.isPresentAtLeastOneCellToMoveTo(game, c)) // if the current worker is not movable
            return returnError(); //report error


        if (((MovePower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) { //if usePower goes well then go to build
            returnContent = new ReturnContent<>();
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.BUILD);
            returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));

            //save
            GameMemory.save((Block) c, Lobby.backupPath);
            GameMemory.save(game.getCurrentPlayer().getCurrentWorker(), game.getCurrentPlayer(), Lobby.backupPath);
        }


        if (returnContent == null) //if usePower went wrong
            return returnError(); //then report error

        return returnContent;
    }

    private ReturnContent buildPower() {
        ReturnContent returnContent = null;

        ReducedDemandCell response = (ReducedDemandCell) game.getRequest().getDemand().getPayload();
        Cell c = game.getBoard().getCell(response.getX(), response.getY());
        Power p = game.getCurrentPlayer().getCard().getPower(0);

        if (c.isComplete()) //if the chosen cell cannot be built up
            return returnError(); //then report error


        if (((BuildPower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) { //if usePower goes well then go to choose worker, end the current turn and start a new one
            returnContent = new ReturnContent<>();
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.CHOOSE_WORKER);
            returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
            returnContent.setChangeTurn(true);

            //save
            GameMemory.save((Block) c, Lobby.backupPath);
        }


        if (returnContent == null) //if usePower went wrong
            return returnError(); //then report error

        return returnContent;
    }
}
