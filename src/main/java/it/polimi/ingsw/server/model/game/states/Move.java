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
import it.polimi.ingsw.server.model.cards.powers.ActivePower;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.*;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

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

        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC " + game.getCurrentPlayer().getMalusList().size());

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
        List<Cell> remainingCells = around.stream()
                .filter(c -> !c.getLevel().equals(Level.DOME))
                .filter(c -> c.isFree() /*|| ((Block) c).getPawn().equals(currentPlayer.getCurrentWorker())*/)
                .filter(c -> (c.getLevel().toInt() - cellToMoveTo.getLevel().toInt() <= 1))
                .collect(Collectors.toList());

        if (remainingCells.isEmpty()) return false;
        if (game.getCurrentPlayer().getMalusList().isEmpty()) return true;

        return remainingCells.stream().anyMatch(c -> ActivePower.verifyMalus(game.getCurrentPlayer().getMalusList(), cellToMoveTo, c)) || game.getCurrentPlayer().getMalusList().get(0).isPermanent();

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
        ReturnContent returnContent;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());

        //validate input
        if (cellToMoveTo == null)
            return returnError();


        if (game.getRequest().getDemand().getHeader().equals(DemandType.USE_POWER)) //if it is asked to use a power
            returnContent = usePower(); //then usePower
        else
           returnContent = move(); //else it must be a move (verified in Controller), so move!


        if(ChangeTurn.controlWinCondition(game)) { //if the current player has won, then notify its victory to everyone!
            returnContent.setState(State.VICTORY);
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setPayload(new ReducedPlayer(currentPlayer.getNickName()));
        }


        if (!returnContent.getAnswerType().equals(AnswerType.ERROR)) { //if the action was successful, then save!
            //save
            GameMemory.save((Block) cellToMoveTo, Lobby.backupPath);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.backupPath);
            GameMemory.save(game.getPlayerList(), Lobby.backupPath);
        }


        Power p = currentPlayer.getCard().getPower(0);
        if (p.getEffect().equals(Effect.MALUS) && returnContent.getAnswerType().equals(AnswerType.SUCCESS)) { //if the current player's god has a malus power
            if (ActivePower.verifyMalus((Malus) p.getAllowedAction(), currentPlayer.getCurrentWorker())) { //then if the current player has activated its god's personal malus
                ChooseCard.applyMalus(game, Timing.END_TURN); //then apply it

                //save
                GameMemory.save(game.getPlayerList(), Lobby.backupPath);
            }
        }

        //save
        GameMemory.save(game.parseState(returnContent.getState()), Lobby.backupPath);

        return returnContent;
    }

    private ReturnContent returnError() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.MOVE);

        return returnContent;
    }

    private ReturnContent move() {
        ReturnContent returnContent = null;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());

        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC " + currentPlayer.getMalusList().size());

        // it checks if the chosen cell is in the possible moves, otherwise the player has to move again
        if (isMoveCorrect(cellToMoveTo)) {
            game.getBoard().move(currentPlayer, cellToMoveTo);
            //if the worker is moved to a third level (from a second one), the player that moved wins

            returnContent = new ReturnContent<>();

            if (reachedThirdLevel(game)) { //if the current worker reached the third level
                returnContent.setAnswerType(AnswerType.VICTORY); //go to victory because the current player has won
                returnContent.setState(State.VICTORY);
                //returnContent.setPayload(PreparePayload.addChangedCells(game, State.MOVE));
                returnContent.setPayload(new ReducedPlayer(currentPlayer.getNickName()));
            }
            else { //else, which means that no one won in this turn, the game switches to the next state
                returnContent.setAnswerType(AnswerType.SUCCESS);

                Effect effect = game.getCurrentPlayer().getCard().getPower(0).getEffect();
                if (effect.equals(Effect.MOVE) && game.getCurrentPlayer().getCard().getPower(0).getTiming().equals(Timing.ADDITIONAL)) //if the current player's god has an additional power
                    returnContent = additionalPower(); //then evaluate if it can be used (go to AskAdditionalPower if it can be used, go to build otherwise)
                else {
                    returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE)); //else go to build
                    returnContent.setState(State.BUILD);
                }
            }
        }

        if (returnContent == null)
            return returnError();

        return returnContent;
    }

    private ReturnContent usePower() {
        ReturnContent returnContent;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());

        Power p = game.getCurrentPlayer().getCard().getPower(0);

        if (p.getEffect().equals(Effect.MOVE) && ((MovePower) p).usePower(currentPlayer, cellToMoveTo, game.getBoard().getAround(cellToMoveTo))) //if it's a movePower and usePower goes well
            returnContent = movePower(); //then evaluate next state (if it can move multiple time then stay in move else go to build)
        else if (p.getEffect().equals(Effect.BUILD) && p.getPersonalMalus() != null && p.getPersonalMalus().getMalusType().equals(MalusType.MOVE) &&
                ((BuildPower) p).usePower(currentPlayer, cellToMoveTo, game.getBoard().getAround(cellToMoveTo))) { //else if it's a build power with a move malus and usePower goes well
            returnContent = buildPower(); //go to build
        }
        else //if it isn't an active power or usePower doesn't go well
            returnContent = returnError(); //then report error

        return returnContent;
    }

    private ReturnContent movePower() {
        ReturnContent returnContent = new ReturnContent<>();

        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());

        Power p = game.getCurrentPlayer().getCard().getPower(0);

        returnContent.setAnswerType(AnswerType.SUCCESS);

        if (((MovePower) p).getNumberOfActionsRemaining() == -1 && p.getConstraints().getNumberOfAdditional() == -1) { //if the current player can move multiple times
            returnContent.setState(State.MOVE); //then remain in move
            returnContent.setPayload(PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.MOVE));
        }
        else { //else go to build
            returnContent.setState(State.BUILD);
            returnContent.setPayload(PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));
        }

        //save
        GameMemory.save((Block) cellToMoveTo, Lobby.backupPath);

        return returnContent;
    }

    private ReturnContent buildPower() {
        ReturnContent returnContent = new ReturnContent<>();

        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());
        List<ReducedAnswerCell> payload;

        returnContent.setAnswerType(AnswerType.SUCCESS);
        returnContent.setState(State.MOVE);
        payload = PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.MOVE);
        payload.add(ReducedAnswerCell.prepareCell(cellToMoveTo, game.getPlayerList()));

        returnContent.setPayload(payload);

        //save
        GameMemory.save((Block) cellToMoveTo, Lobby.backupPath);
        GameMemory.save(game.parseState(State.MOVE), Lobby.backupPath);

        return returnContent;
    }

    private ReturnContent additionalPower() {
        ReturnContent returnContent = new ReturnContent<>();

        List<ReducedAnswerCell> payload;

        returnContent.setAnswerType(AnswerType.SUCCESS);
        payload = PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER);
        PreparePayload.mergeReducedAnswerCellList(payload, PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));

        if (payload.stream()
                .map(ReducedAnswerCell::getActionList)
                .flatMap(List::stream)
                .distinct()
                .allMatch(action -> action.equals(ReducedAction.DEFAULT))//if there are no cell where the additional power can be used
        )
            returnContent.setState(State.BUILD); //then go to build
        else
            returnContent.setState(State.ASK_ADDITIONAL_POWER); //else ask if the current player wants to use the additional power

        returnContent.setPayload(payload);

        return returnContent;
    }
}