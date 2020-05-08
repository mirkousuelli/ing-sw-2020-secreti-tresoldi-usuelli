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
import it.polimi.ingsw.server.model.cards.powers.Power;
import it.polimi.ingsw.server.model.cards.powers.WinConditionPower;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;

public class ChangeTurn implements GameState {
    /* @Class
     * it represents the state where the current player changes and the win conditions are checked
     */

    private final Game game;

    public ChangeTurn(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }


    private void changeCurrentPlayer() {
        /* @function
         * it switches the player that must play
         */

        int index = (game.getIndex(game.getCurrentPlayer()) + 1) % game.getNumPlayers();

        game.setCurrentPlayer(game.getPlayerList().get(index));
    }


    private boolean onePlayerRemaining(){
        return game.getNumPlayers() == 1;
    }


    private boolean controlWinCondition() {
        /* @predicate
         * it checks if any win condition is verified (some God powers add a secondary win condition)
         */

        if (game.getPrevState() == null ||
            game.getCurrentPlayer().getCard() == null) return false;

        Power p = game.getCurrentPlayer().getCard().getPower(0);

        return p.getEffect().equals(Effect.WIN_COND) && ((WinConditionPower) p).usePower(game);
    }

    @Override
    public String getName() {
        return State.CHANGE_TURN.toString();
    }

    @Override
    public ReturnContent gameEngine() {
        ReturnContent returnContent = new ReturnContent();

        returnContent.setAnswerType(AnswerType.ERROR);
        returnContent.setState(State.CHANGE_TURN);
        game.getCurrentPlayer().removeMalus();

        if (game.getPrevState() != null && game.getPrevState().equals(State.BUILD))
            ChooseCard.applyMalus(game, Timing.END_TURN);

        // Check if any win condition is verified (or if only one player remains); if so the game goes to Victory state
        if(controlWinCondition() || onePlayerRemaining()) {
            returnContent.setAnswerType(AnswerType.VICTORY);
            returnContent.setState(State.VICTORY);
        }
        else {
            // Otherwise the current player is changed and the game goes to ChooseWorker state

            changeCurrentPlayer();
            returnContent.setAnswerType(AnswerType.SUCCESS);
            returnContent.setPayload(new ReducedPlayer(game.getCurrentPlayer().nickName));

        }



        return returnContent;
    }
}