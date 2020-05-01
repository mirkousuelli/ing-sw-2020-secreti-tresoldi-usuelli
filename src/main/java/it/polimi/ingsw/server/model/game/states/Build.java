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
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;

import java.util.ArrayList;
import java.util.List;

public class Build implements GameState {
    /* @abstractClass
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
        List<Cell> specialBuilds = game.getBoard().getPossibleBuilds(currentPlayer.getCurrentWorker());
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToBuildUp = new Block(cell.getX(), cell.getY());


        Cell chosenCell;
        List<Cell> changedCells = new ArrayList<>();
        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.BUILD);


        for (Cell c : specialBuilds) {
            if (c.getX() == cellToBuildUp.getX() && c.getY() == cellToBuildUp.getY()) {
                for (Power p : currentPlayer.getCard().getPowerList()) {
                    if (p.getEffect().equals(Effect.BUILD)) {
                        if (((BuildPower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) {
                            switch (p.getTiming()) {
                                case DEFAULT:
                                    returnContent.setAnswerType(AnswerType.SUCCESS);
                                    returnContent.setState(State.CHANGE_TURN);
                                    chosenCell = currentPlayer.getCurrentWorker().getPreviousBuild();
                                    changedCells.add(chosenCell);
                                    returnContent.setPayload(changedCells);
                                    break;

                                case ADDITIONAL:
                                    returnContent.setAnswerType(AnswerType.SUCCESS);
                                    chosenCell = currentPlayer.getCurrentWorker().getPreviousBuild();
                                    changedCells.add(chosenCell);
                                    returnContent.setPayload(changedCells);
                                    break;

                                default:
                                    break;
                            }
                        }

                        return returnContent;
                    }
                }
            }
        }


        chosenCell = game.getBoard().getCell(cellToBuildUp.getX(), cellToBuildUp.getY());
        // it shows the possible cells where the player can build and then allows him to choose one
        // System.out.println(possibleBuilds);
        game.getBoard().build(game.getCurrentPlayer(), chosenCell);

        // if the player chose a possible cell, the game actually builds on it and then proceed to change the turn
        if (isBuildPossible(chosenCell)) {
            game.getBoard().build(game.getCurrentPlayer(), chosenCell);

            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setState(State.CHANGE_TURN);
            chosenCell = currentPlayer.getCurrentWorker().getPreviousBuild();
            changedCells.add(chosenCell);
            returnContent.setPayload(changedCells);
        }
        // if the player selects a cell where he cannot build, he has to build again

        return returnContent;
    }
}