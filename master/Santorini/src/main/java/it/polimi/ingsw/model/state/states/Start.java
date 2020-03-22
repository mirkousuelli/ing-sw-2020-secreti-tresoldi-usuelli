/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.state.states;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

import java.util.List;

public class Start implements GameState {
    /* @abstractClass
     * it represents the state that include all the actions to initialize the game
     */

    public Start(Game game) {
        /* @constructor
         * it allows the game to begin
         */
    }

    private Player PickChallenger(String[] Nicknames) {
        /* @function
         * it selects who is the Challenger (randomly or by picking him)
         */
        return null;
    }

    private List<Card> pickCards() {
        /* @function
         * the Challenger must choose the cards (based on the number of players) between which every player has to choose his own
         */
        return null;
    }

    private void distributeCard() {
        /* @function
         * every player chooses a card between the card picked by the Challenger
         */
    }

    private Player chooseStarter() {
        /* @function
         * the Challenger decides who starts first
         */
        return null;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}