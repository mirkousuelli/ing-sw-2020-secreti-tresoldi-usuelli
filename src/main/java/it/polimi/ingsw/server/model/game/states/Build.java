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
import it.polimi.ingsw.communication.message.payload.ReducedLevel;
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

import java.util.ArrayList;
import java.util.List;

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
        ReturnContent returnContent = new ReturnContent();

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToBuildUp = new Block(cell.getX(), cell.getY());

        Cell chosenCell;
        List<Cell> changedCells = new ArrayList<>();


        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.BUILD);

        List<ReducedAnswerCell> toReturn = new ArrayList<>();

        if (game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER)) {
            Power p = game.getCurrentPlayer().getCard().getPower(0);
            Cell c = game.getBoard().getCell(cellToBuildUp.getX(), cellToBuildUp.getY());

            if (((BuildPower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) {
                ReducedAnswerCell temp = new ReducedAnswerCell(c.getX(), c.getY());
                temp.setAction(ReducedAction.DEFAULT);
                temp.setLevel(ReducedLevel.parseInt(c.getLevel().toInt()));
                toReturn.add(temp);

                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.CHOOSE_WORKER);
                returnContent.setChangeTurn(true);
                returnContent.setPayload(toReturn);
            }
        }
        else {
            chosenCell = game.getBoard().getCell(cellToBuildUp.getX(), cellToBuildUp.getY());

            // if the player chose a possible cell, the game actually builds on it and then proceed to change the turn
            if (isBuildPossible(chosenCell)) {
                game.getBoard().build(game.getCurrentPlayer(), chosenCell);

                returnContent.setAnswerType(AnswerType.SUCCESS);
                chosenCell = currentPlayer.getCurrentWorker().getPreviousBuild();
                changedCells.add(chosenCell);

                Power p = game.getCurrentPlayer().getCard().getPower(0);
                if (p.getEffect().equals(Effect.BUILD) && p.getTiming().equals(Timing.ADDITIONAL)) {
                    returnContent.setState(State.ADDITIONAL_POWER);
                    toReturn = Move.preparePayloadBuild(game, Timing.ADDITIONAL, State.BUILD);
                }
                else {
                    returnContent.setState(State.CHOOSE_WORKER);
                    returnContent.setChangeTurn(true);
                    toReturn = new ArrayList<>();
                }

                for (Cell c : changedCells)
                    toReturn.add(ReducedAnswerCell.prepareCell(c, game.getPlayerList()));

                returnContent.setPayload(toReturn);
            }

        }
        return returnContent;
    }
}