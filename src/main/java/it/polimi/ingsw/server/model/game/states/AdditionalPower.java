package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
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
import it.polimi.ingsw.server.network.message.Lobby;

import java.util.ArrayList;

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
        ReturnContent returnContent = new ReturnContent();

        ReducedDemandCell response = (ReducedDemandCell) game.getRequest().getDemand().getPayload();
        Cell c = game.getBoard().getCell(response.getX(), response.getY());
        State prevState = game.getPrevState();
        Power p = game.getCurrentPlayer().getCard().getPower(0);

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.ADDITIONAL_POWER);

        System.out.println(getName());

        if (response.getX() == -1 && response.getY() == -1) {
            returnContent.setAnswerType(AnswerType.SUCCESS);
            if (prevState.equals(State.MOVE)) {
                returnContent.setState(State.BUILD);
                returnContent.setPayload(Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
            }
            else if (prevState.equals(State.BUILD)) {
                returnContent.setState(State.CHOOSE_WORKER);
                returnContent.setChangeTurn(true);
                returnContent.setPayload(new ArrayList<ReducedAnswerCell>());
            }
        }
        else {
            if (c == null)
                return returnContent;

            if (prevState.equals(State.MOVE)) {
                if (!Move.isPresentAtLeastOneCellToMoveTo(game, c))
                    return returnContent;

                if (((MovePower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) {
                    returnContent.setAnswerType(AnswerType.SUCCESS);
                    returnContent.setState(State.BUILD);
                    returnContent.setPayload(Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));

                    GameMemory.save((Block) c, Lobby.backupPath);
                    GameMemory.save(game.getCurrentPlayer().getCurrentWorker(), game.getCurrentPlayer(), Lobby.backupPath);
                }
            }
            else if (prevState.equals(State.BUILD)) {
                if (c.isComplete())
                    return returnContent;

                if (((BuildPower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) {
                    returnContent.setAnswerType(AnswerType.SUCCESS);
                    returnContent.setState(State.CHOOSE_WORKER);
                    returnContent.setPayload(Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
                    returnContent.setChangeTurn(true);

                    GameMemory.save((Block) c, Lobby.backupPath);
                }
            }
        }

        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }
}
