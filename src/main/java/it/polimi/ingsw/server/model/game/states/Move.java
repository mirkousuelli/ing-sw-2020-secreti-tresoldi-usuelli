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
import it.polimi.ingsw.communication.message.payload.*;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.map.Level;

import java.util.ArrayList;
import java.util.List;

public class Move implements GameState {
    /* @Class
     * it represents the state where a player has to move at least one of his worker
     */

    private final Game game;

    public Move(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    private boolean isMoveCorrect(Cell cellToMoveTo) {
        /* @predicate
         * it tells if the cell chosen by the player is a cell where he can actually move to
         */

        List<Cell> possibleMoves = game.getBoard().getPossibleMoves(game.getCurrentPlayer());

        for (Cell c: possibleMoves) {
            if (c.getX() == cellToMoveTo.getX() && c.getY() == cellToMoveTo.getY())
                return true;
        }

        return false;
    }

    private boolean reachedThirdLevel(Game game) {
        /* @predicate
         * it tells if a worker reached the third level
         */

        return game.getCurrentPlayer().getCurrentWorker().getLevel() == Level.TOP;
    }


    @Override
    public String getName() {
        return State.MOVE.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent();

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.MOVE);

        if (cellToMoveTo == null)
            return returnContent;


        if (game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER)) {
            Power p = game.getCurrentPlayer().getCard().getPower(0);

            if (((MovePower) p).usePower(game.getCurrentPlayer(), cellToMoveTo, game.getBoard().getAround(cellToMoveTo))) {
                returnContent.setAnswerType(AnswerType.SUCCESS);

                if (((MovePower) p).getNumberOfActionsRemaining() == -1 && p.getConstraints().getNumberOfAdditional() == -1) {
                    returnContent.setAnswerType(AnswerType.SUCCESS);
                    returnContent.setState(State.MOVE);
                    returnContent.setPayload(ChooseWorker.preparePayloadMove(game, Timing.DEFAULT, State.MOVE));
                }
                else {
                    returnContent.setState(State.BUILD);
                    returnContent.setPayload(Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
                }
            }
        }
        else {
            // if the curPlayer cannot move with the chosen worker, he gets to choose a different one and the game goes to ChooseWorker state
            //if (currentPlayer.getCurrentWorker().isMovable()) { // if the worker can be moved, the player is showed the cells he can move to and moves to one of them
                // it checks if the chosen cell is in the possible moves, otherwise the player has to move again
                if (isMoveCorrect(cellToMoveTo)) {
                    game.getBoard().move(currentPlayer, cellToMoveTo);
                    //if the worker is moved to a third level (from a second one), the player that moved wins
                    if (reachedThirdLevel(game)) {
                        returnContent.setAnswerType(AnswerType.VICTORY);
                        returnContent.setState(State.VICTORY);
                    } else { //which means that no one won in this turn, the game switches to UsePower state
                        returnContent.setAnswerType(AnswerType.SUCCESS);

                        Effect effect = game.getCurrentPlayer().getCard().getPower(0).getEffect();
                        if (effect.equals(Effect.MOVE) && game.getCurrentPlayer().getCard().getPower(0).getTiming().equals(Timing.ADDITIONAL)) {
                            returnContent.setPayload(ChooseWorker.preparePayloadMove(game, Timing.ADDITIONAL, State.MOVE));
                            returnContent.setState(State.ADDITIONAL_POWER);

                            return returnContent;
                        }

                        returnContent.setPayload(Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
                        returnContent.setState(State.BUILD);
                    }
                }
            //}
        }

        return returnContent;
    }

    public static List<ReducedAnswerCell> preparePayloadBuild(Game game, Timing timing, State state) {
        List<Cell> possibleBuilds;
        ReducedAnswerCell temp;
        List<ReducedAnswerCell> tempList = new ArrayList<>();

        if (state.equals(State.MOVE)) {
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker()));
            temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getPlayerList());
            tempList.add(temp);

            temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getPreviousLocation(), game.getPlayerList());
            tempList.add(temp);
        }
        else
            possibleBuilds = new ArrayList<>();

        List<Cell> specialBuilds = new ArrayList<>(game.getBoard().getSpecialBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        List<ReducedAnswerCell> toReturn = ReducedAnswerCell.prepareList(ReducedAction.BUILD, game.getPlayerList(), possibleBuilds, specialBuilds);

        boolean found;
        for (ReducedAnswerCell tc : tempList) {
            found = false;
            for (ReducedAnswerCell rc : toReturn) {
                if (rc.getX() == tc.getX() && rc.getY() == tc.getY()) {
                    found = true;
                    break;
                }
            }

            if (!found)
                toReturn.add(tc);
        }

        return toReturn;
    }
}