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
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.ActivePower;
import it.polimi.ingsw.server.model.cards.powers.BuildPower;
import it.polimi.ingsw.server.model.cards.powers.MovePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.*;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents the state where must move his worker
 */
public class Move implements GameState {

    private final Game game;

    /**
     * Constructor of the state Move
     *
     * @param game the game which the state is connected to
     */
    public Move(Game game) {
        this.game = game;
    }

    private void move(Worker currentWorker, Cell cellToMoveTo) {
        currentWorker.getLocation().removePawn();
        currentWorker.setPreviousLocation((currentWorker.getLocation()));
        currentWorker.setLocation((Block) cellToMoveTo);
    }

    private boolean checkIfContained(List<Cell> possibleMoves, Cell cell) {
        for (Cell c: possibleMoves) {
            if (c.getX() == cell.getX() && c.getY() == cell.getY())
                return true;
        }

        return false;
    }

    /**
     * Method that tells if the cell chosen by the player is a cell where he can actually move to
     *
     * @param cellToMoveTo the cell where the player wants to move to
     * @return {@code true} if the player can move to the chosen cell, {@code false} otherwise
     */
    private boolean isMoveCorrect(Cell cellToMoveTo) {
        Player currentPlayer = game.getCurrentPlayer();
        Worker currentWorker = currentPlayer.getCurrentWorker();
        Board board = game.getBoard();

        if (!cellToMoveTo.isWalkable()) return false;
        if (cellToMoveTo.getLevel().toInt() > currentWorker.getLocation().getLevel().toInt() + 1) return false;

        List<Cell> possibleMoves = game.getBoard().getPossibleMoves(currentPlayer);

        if (checkIfContained(possibleMoves, cellToMoveTo)) return true;

        if (currentPlayer.getMalusList().stream().anyMatch(Malus::isPermanent)) {
            List<ReducedAnswerCell> payload = PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.MOVE).stream()
                    .filter(reducedAnswerCell -> reducedAnswerCell.getActionList().contains(ReducedAction.MOVE))
                    .filter(ReducedAnswerCell::isFree)
                    .collect(Collectors.toList());

            if (payload.isEmpty())
                return false;
            else {
                payload.forEach(reducedAnswerCell -> possibleMoves.add(board.getCell(reducedAnswerCell.getX(), reducedAnswerCell.getY())));
                if (checkIfContained(possibleMoves, cellToMoveTo))
                    move(currentWorker, cellToMoveTo);
            }
        }

        return checkIfContained(possibleMoves, cellToMoveTo);
    }

    /**
     * Method that controls if a worker reached the third level, which is the default win condition of every player
     *
     * @param game the game where the control is made
     * @return {@code true} if a worker reached the third level, {@code false} otherwise
     */
    static boolean reachedThirdLevel(Game game) {
        return game.getCurrentPlayer().getCurrentWorker().getLevel().equals(Level.TOP);
    }

    /**
     * Method that checks if there is at least one cell to move to from the chosen cell
     *
     * @param game the current game
     * @param cellToMoveTo the cell from which the control is made
     * @return {@code false} if there are no cells to move to from the given cell, {@code true} otherwise
     */
    public static boolean isPresentAtLeastOneCellToMoveTo(Game game, Cell cellToMoveTo) {
        Player currentPlayer = game.getCurrentPlayer();
        List<Malus> currentPlayerMalusList = currentPlayer.getMalusList();
        List<Cell> around = game.getBoard().getAround(cellToMoveTo);

        List<Cell> remainingCells = around.stream()
                .filter(c -> !c.getLevel().equals(Level.DOME))
                .filter(c -> c.isFree() || ((Block) c).getPawn().equals(currentPlayer.getCurrentWorker()))
                .filter(c -> (c.getLevel().toInt() - cellToMoveTo.getLevel().toInt() <= 1))
                .collect(Collectors.toList());

        if (remainingCells.isEmpty()) return false;
        if (remainingCells.size() == 1 && !remainingCells.get(0).isFree() && (!((Block) remainingCells.get(0)).getPawn().equals(game.getCurrentPlayer().getCurrentWorker()) || game.getState().getName().equals(State.CHOOSE_WORKER.toString()))) return false;
        if (currentPlayerMalusList.isEmpty()) return true;

        return remainingCells.stream().anyMatch(c -> ActivePower.verifyMalus(currentPlayerMalusList, cellToMoveTo, c)) || currentPlayerMalusList.get(0).isPermanent();
    }

    @Override
    public String getName() {
        return State.MOVE.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here the player picks a cell where he wants to move to and, if the cell is one where he can actually move to,
     * the board is updated and the state changes accordingly
     * <p>
     * After the move, it is controlled if any win condition is verified (because both the default one and Pan's power
     * could be verified)
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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


        Player victorious = ChangeTurn.controlWinCondition(game);
        if(victorious != null) { //if the current player has won, then notify its victory to everyone!
            returnContent.setState(State.VICTORY);
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setPayload(new ReducedPlayer(victorious.getNickName()));
        }


        if (!returnContent.getAnswerType().equals(AnswerType.ERROR)) { //if the action was successful, then save!
            //save
            GameMemory.save((Block) cellToMoveTo, Lobby.BACKUP_PATH);
            GameMemory.save(currentPlayer.getCurrentWorker(), currentPlayer, Lobby.BACKUP_PATH);
            GameMemory.save(game.getPlayerList(), Lobby.BACKUP_PATH);
        }


        Power p = currentPlayer.getCard().getPower(0);
        if (p.getEffect().equals(Effect.MALUS) && returnContent.getAnswerType().equals(AnswerType.SUCCESS) && //if the current player's god has a malus power
          ActivePower.verifyMalus((Malus) p.getAllowedAction(), currentPlayer.getCurrentWorker())) { //and if the current player has activated its god's personal malus
            ChooseCard.applyMalus(game, Timing.END_TURN); //then apply it

            //save
            GameMemory.save(game.getPlayerList(), Lobby.BACKUP_PATH);
        }

        //save
        GameMemory.save(game.parseState(returnContent.getState()), Lobby.BACKUP_PATH);
        GameMemory.save(currentPlayer, State.MOVE, Lobby.BACKUP_PATH);

        return returnContent;
    }

    /**
     * Method that returns an error if the player picked a cell where he cannot move to and has to pick another cell
     *
     * @return returnContent, containing an answer of error and the state that remains the same
     */
    private ReturnContent returnError() {
        ReturnContent returnContent = new ReturnContent<>();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.MOVE);

        return returnContent;
    }

    /**
     * Method that actually make the move when the chosen cell is one where he can move to, controlling then if the
     * current worker reached the third level
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent move() {
        ReturnContent returnContent = null;

        Player currentPlayer = game.getCurrentPlayer();
        ReducedDemandCell cell = ((ReducedDemandCell) game.getRequest().getDemand().getPayload());
        Cell cellToMoveTo = game.getBoard().getCell(cell.getX(), cell.getY());

        // it checks if the chosen cell is in the possible moves, otherwise the player has to move again
        if (isMoveCorrect(cellToMoveTo)) {
            game.getBoard().move(currentPlayer, cellToMoveTo);
            //if the worker is moved to a third level (from a second one), the player that moved wins

            returnContent = new ReturnContent<>();

            if (Move.reachedThirdLevel(game)) { //if the current worker reached the third level
                returnContent.setAnswerType(AnswerType.VICTORY); //go to victory because the current player has won
                returnContent.setState(State.VICTORY);
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

    /**
     * Method that allows a player to use the special power (it can be both a move or a build special power);
     * if the action is made correctly then the current state changes depending on the action, otherwise he has to pick
     * another cell where to move to
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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

    /**
     * Method that allows a player to use a special move power
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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
        GameMemory.save((Block) cellToMoveTo, Lobby.BACKUP_PATH);

        return returnContent;
    }

    /**
     * Method that allows a player to use a special build power
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
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
        GameMemory.save((Block) cellToMoveTo, Lobby.BACKUP_PATH);
        GameMemory.save(game.parseState(State.MOVE), Lobby.BACKUP_PATH);

        return returnContent;
    }

    /**
     * Method that allows the player to choose if he wants to use an additional power (if there is at least one cell
     * where the additional power can be used) or automatically swaps to Build state otherwise
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    private ReturnContent additionalPower() {
        ReturnContent returnContent = new ReturnContent<>();

        List<ReducedAnswerCell> payload;

        returnContent.setAnswerType(AnswerType.SUCCESS);
        payload = PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER);
        payload = PreparePayload.mergeReducedAnswerCellList(payload, PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));

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