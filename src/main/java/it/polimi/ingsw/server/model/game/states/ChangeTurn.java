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
import it.polimi.ingsw.communication.message.payload.ReducedPlayer;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.powers.ActivePower;
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.WinConditionPower;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;


/**
 * Class that represents the state where the current player changes and the win conditions are checked
 */
public class ChangeTurn implements GameState {

    private final Game game;

    /**
     * Constructor of the state ChangeTurn
     *
     * @param game the game which the state is connected to
     */
    public ChangeTurn(Game game) {
        this.game = game;
    }

    /**
     * Method that actually changes the current player
     */
    private void changeCurrentPlayer() {
        int index = (game.getIndex(game.getCurrentPlayer()) + 1) % game.getNumPlayers();

        game.setCurrentPlayer(game.getPlayerList().get(index));
    }

    /**
     * Method that controls if there's only one player remaining
     *
     * @return {@code true} if only one player is left in the game, {@code false} otherwise
     */
    private boolean onePlayerRemaining(){
        return game.getNumPlayers() == 1;
    }

    /**
     * Method that controls if any win condition is verified (some God powers add a secondary win condition)
     *
     * @param game the game where to check
     * @return {@code true} if any win condition is verified, {@code false} if not
     */
    public static boolean controlWinCondition(Game game) {
        if (game.getPrevState() == null ||
            game.getCurrentPlayer().getCard() == null ||
            game.getCurrentPlayer().getCurrentWorker() == null ||
            game.getCurrentPlayer().getCurrentWorker().getPreviousLocation() == null) return false;

        Power p = game.getCurrentPlayer().getCard().getPower(0);

        if (p.getEffect().equals(Effect.BUILD) || p.getEffect().equals(Effect.MOVE)) ((ActivePower) p).setNumberOfActionsRemaining();

        return p.getEffect().equals(Effect.WIN_COND) && ((WinConditionPower) p).usePower(game);
    }

    @Override
    public String getName() {
        return State.CHANGE_TURN.toString();
    }

    /**
     * Method that represents the engine of the game and works differently depending on the current state
     * <p>
     * In here, after checking that no player won during this turn, the current player is changed and the turn is
     * set to ChooseWorker
     *
     * @return returnContent, which contains information like the outcome of the actions and the next state
     */
    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent<>();
        Player currentPlayer = game.getCurrentPlayer();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHANGE_TURN);

        if (!currentPlayer.getMalusList().isEmpty() && (game.getPrevState().equals(State.ASK_ADDITIONAL_POWER) || game.getPrevState().equals(State.MOVE))) {
            currentPlayer.getMalusList().stream()
                    .filter(malus -> !malus.isPermanent())
                    .forEach(malus -> malus.setNumberOfTurnsUsed(malus.getNumberOfTurnsUsed() + 1));
            currentPlayer.removeMalus();

            //save
            GameMemory.save(currentPlayer, Lobby.backupPath);
        }

        // Check if any win condition is verified (or if only one player remains); if so the game goes to Victory state
        if(controlWinCondition(game) || onePlayerRemaining()) {
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setState(State.VICTORY);
        }
        else {
            // Otherwise the current player is changed and the game goes to ChooseWorker state
            changeCurrentPlayer();
            returnContent.setAnswerType(AnswerType.CHANGE_TURN);
            returnContent.setPayload(new ReducedPlayer(game.getCurrentPlayer().nickName));
        }

        if (game.getPrevState() != null && !game.getPrevState().equals(State.START) &&
            !game.getPrevState().equals(State.CHOOSE_CARD) && !game.getPrevState().equals(State.CHOOSE_STARTER) &&
            !game.getPrevState().equals(State.PLACE_WORKERS) && !game.getPrevState().equals(State.CHANGE_TURN)) {
            GameMemory.save(game.getCurrentPlayer(), State.CHOOSE_WORKER, Lobby.backupPath);
        }

        return returnContent;
    }
}