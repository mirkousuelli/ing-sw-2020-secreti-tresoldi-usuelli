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
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

public class Move implements GameState {

    /* @abstractClass
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
        List<Cell> specialMoves = new ArrayList<>();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = new Block(cell.getX(), cell.getY());

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.MOVE);


        List<Cell> specialMovesDef = game.getBoard().getSpecialMoves(currentPlayer.getCurrentWorker().getLocation(), currentPlayer, Timing.DEFAULT);
        if (specialMovesDef != null)
            specialMoves.addAll(specialMovesDef);
        List<Cell> specialMovesAdd = game.getBoard().getSpecialMoves(currentPlayer.getCurrentWorker().getLocation(), currentPlayer, Timing.ADDITIONAL);
        if (specialMovesAdd != null)
            specialMoves.addAll(specialMovesAdd);

        for (Cell c: specialMoves) {
            if (c.getX() == cellToMoveTo.getX() && c.getY() == cellToMoveTo.getY()) {
                for (Power p: currentPlayer.getCard().getPowerList()) {
                    if (p.getEffect().equals(Effect.MOVE)) {
                        if (((MovePower) p).usePower(game.getCurrentPlayer(), c, game.getBoard().getAround(c))) {
                            switch (p.getTiming()) {
                                case DEFAULT:
                                    returnContent.setAnswerType(AnswerType.SUCCESS);
                                    returnContent.setState(State.BUILD);
                                    returnContent.setPayload(preparePayloadBuild(Timing.DEFAULT));
                                    break;

                                case ADDITIONAL:
                                    returnContent.setAnswerType(AnswerType.SUCCESS);
                                    returnContent.setPayload(preparePayloadBuild(Timing.ADDITIONAL));
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



        // if the curPlayer cannot move with the chosen worker, he gets to choose a different one and the game goes to ChooseWorker state
        if (currentPlayer.getCurrentWorker().isMovable()) { // if the worker can be moved, the player is showed the cells he can move to and moves to one of them
            // it checks if the chosen cell is in the possible moves, otherwise the player has to move again
            if(isMoveCorrect(cellToMoveTo)) {
                game.getBoard().move(currentPlayer, game.getBoard().getCell(cellToMoveTo.getX(), cellToMoveTo.getY()));
                //if the worker is moved to a third level (from a second one), the player that moved wins
                if (reachedThirdLevel(game)) {
                    returnContent.setAnswerType(AnswerType.VICTORY);
                    returnContent.setState(State.VICTORY);
                }
                else { //which means that no one won in this turn, the game switches to Build state
                    returnContent.setAnswerType(AnswerType.SUCCESS);
                    returnContent.setState(State.BUILD);
                }

                returnContent.setPayload(preparePayloadBuild(Timing.DEFAULT));
            }
        }

        return returnContent;
    }

    private List<ReducedAnswerCell> preparePayloadBuild(Timing timing) {
        List<Cell> possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker()));
        List<Cell> specialBuilds = new ArrayList<>(game.getBoard().getSpecialBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        List<ReducedAnswerCell> reducedAround = ReducedAnswerCell.prepareList(ReducedAction.BUILD, game.getPlayerList(), possibleBuilds, specialBuilds);
        List<ReducedAnswerCell> tempList = new ArrayList<>();
        ReducedAnswerCell temp;

        temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getPlayerList());
        temp.setAction(ReducedAction.DEFAULT);
        tempList.add(temp);

        temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getPreviousLocation(), game.getPlayerList());
        temp.setAction(ReducedAction.DEFAULT);
        tempList.add(temp);


        for (ReducedAnswerCell rc : reducedAround) {
            for (ReducedAnswerCell c : tempList) {
                if (rc.getX() != c.getX() || rc.getY() != c.getY()) {
                    reducedAround.add(temp);
                }
            }
        }


        return reducedAround;
    }
}