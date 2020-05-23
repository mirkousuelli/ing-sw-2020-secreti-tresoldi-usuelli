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
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.message.Lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static boolean isPresentAtLeastOneCellToMoveTo(Game game, Cell cellToMoveTo, List<Cell> around) {
        Player currentPlayer = game.getCurrentPlayer();

        return around.stream()
                .filter(c -> !c.getLevel().equals(Level.DOME))
                .filter(c -> c.isFree() || ((Block) c).getPawn().equals(currentPlayer.getCurrentWorker()))
                .anyMatch(c -> (c.getLevel().toInt() - cellToMoveTo.getLevel().toInt() <= 1));

    }

    public static boolean isPresentAtLeastOneCellToMoveTo(Game game, Cell cellToMoveTo) {
        return Move.isPresentAtLeastOneCellToMoveTo(game, cellToMoveTo, game.getBoard().getAround(cellToMoveTo));
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
        List<ReducedAnswerCell> payload = new ArrayList<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.MOVE);

        //validate input
        if (cellToMoveTo == null)
            return returnContent;
        if (!Move.isPresentAtLeastOneCellToMoveTo(game, cellToMoveTo))
            return returnContent;


        if (game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER)) {
            Power p = game.getCurrentPlayer().getCard().getPower(0);

            if (p.getEffect().equals(Effect.MOVE) && ((MovePower) p).usePower(game.getCurrentPlayer(), cellToMoveTo, game.getBoard().getAround(cellToMoveTo))) {
                returnContent.setAnswerType(AnswerType.SUCCESS);

                if (((MovePower) p).getNumberOfActionsRemaining() == -1 && p.getConstraints().getNumberOfAdditional() == -1) {
                    returnContent.setAnswerType(AnswerType.SUCCESS);
                    returnContent.setState(State.MOVE);
                    payload = ChooseWorker.preparePayloadMove(game, Timing.DEFAULT, State.MOVE);
                }
                else {
                    returnContent.setState(State.BUILD);
                    payload = Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE);
                }

                GameMemory.save((Block) cellToMoveTo, Lobby.backupPath);
            }
            else if (p.getEffect().equals(Effect.BUILD) && p.getPersonalMalus() != null && p.getPersonalMalus().getMalusType().equals(MalusType.MOVE) &&
                    ((BuildPower) p).usePower(game.getCurrentPlayer(), cellToMoveTo, game.getBoard().getAround(cellToMoveTo))) {

                returnContent.setAnswerType(AnswerType.SUCCESS);
                returnContent.setState(State.MOVE);
                payload = ChooseWorker.preparePayloadMove(game, Timing.DEFAULT, State.MOVE);
                payload.add(ReducedAnswerCell.prepareCell(cellToMoveTo, game.getPlayerList()));

                GameMemory.save((Block) cellToMoveTo, Lobby.backupPath);
                GameMemory.save(game.parseState(State.MOVE), Lobby.backupPath);
            }
        }
        else {
            // it checks if the chosen cell is in the possible moves, otherwise the player has to move again
            if (isMoveCorrect(cellToMoveTo)) {
                game.getBoard().move(currentPlayer, cellToMoveTo);
                //if the worker is moved to a third level (from a second one), the player that moved wins
                if (reachedThirdLevel(game)) {
                    returnContent.setAnswerType(AnswerType.VICTORY);
                    returnContent.setState(State.VICTORY);
                    payload = addChangedCells(game, State.MOVE);
                }
                else { //which means that no one won in this turn, the game switches to AdditionalPower state
                    returnContent.setAnswerType(AnswerType.SUCCESS);

                    Effect effect = game.getCurrentPlayer().getCard().getPower(0).getEffect();
                    if (effect.equals(Effect.MOVE) && game.getCurrentPlayer().getCard().getPower(0).getTiming().equals(Timing.ADDITIONAL)) {
                        payload = ChooseWorker.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER);


                        System.out.println(payload.stream().map(c -> c.getX() + ", " + c.getY() + c.getActionList() + "\n").collect(Collectors.joining()));

                        if (payload.stream()
                                .map(ReducedAnswerCell::getActionList)
                                .flatMap(List::stream)
                                .distinct()
                                .allMatch(action -> action.equals(ReducedAction.DEFAULT))
                           )
                            returnContent.setState(State.BUILD);
                        else
                            returnContent.setState(State.ADDITIONAL_POWER);
                    }
                    else {
                        payload = Move.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE);
                        returnContent.setState(State.BUILD);
                    }
                }
            }
        }

        if (!returnContent.getAnswerType().equals(AnswerType.ERROR)) {
            returnContent.setPayload(payload);
            GameMemory.save((Block) cellToMoveTo, Lobby.backupPath);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.backupPath);
        }

        if(ChangeTurn.controlWinCondition(game)) {
            returnContent.setState(State.VICTORY);
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setPayload(Move.addChangedCells(game, State.MOVE));
        }

        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }

    public static List<ReducedAnswerCell> preparePayloadBuild(Game game, Timing timing, State state) {
        List<Cell> possibleBuilds;
        List<ReducedAnswerCell> tempList = new ArrayList<>();

        if (state.equals(State.MOVE)) {
            possibleBuilds = new ArrayList<>(game.getBoard().getPossibleBuilds(game.getCurrentPlayer().getCurrentWorker()));
            tempList = addChangedCells(game, State.MOVE);
        }
        else
            possibleBuilds = new ArrayList<>();

        List<Cell> specialBuilds = new ArrayList<>(game.getBoard().getSpecialBuilds(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getCurrentPlayer(), timing));
        List<ReducedAnswerCell> toReturn = ReducedAnswerCell.prepareList(ReducedAction.BUILD, game.getPlayerList(), possibleBuilds, specialBuilds);

        return ChooseWorker.mergeReducedAnswerCellList(toReturn, tempList);
    }

    static List<ReducedAnswerCell> addChangedCells(Game game, State state) {
        ReducedAnswerCell temp;
        List<ReducedAnswerCell> tempList = new ArrayList<>();

        temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getLocation(), game.getPlayerList());
        tempList.add(temp);

        temp = ReducedAnswerCell.prepareCell(game.getCurrentPlayer().getCurrentWorker().getPreviousLocation(), game.getPlayerList());
        if (state.equals(State.MOVE))
            temp.replaceDefaultAction(ReducedAction.BUILD);
        tempList.add(temp);

        return tempList;
    }
}