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
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.state.Game;
import it.polimi.ingsw.model.state.GameState;

public class Start implements GameState {
    /* @abstractClass
     * it represents the state that include all the actions to initialize the game
     */

    public Game game;
    public Player Challenger;
    public Player Starter;

    public Start(Game game) {
        /* @constructor
         * it allows the game to begin
         */

        this.game = game;

        Player challenger = PickChallenger(/* list of nicknames (or list of players) */);
        Deck deck= game.getDeck();
        distributeCard();
        Starter = chooseStarter(challenger);

        // After the inizialisation of the game, it goes to ChooseWorker state
        game.setState(new ChooseWorker(game));

    }

    private String PickChallenger(String[] Nicknames) {
        /* @function
         * it selects who is the Challenger (randomly or by picking him)
         */

       // Challenger = new Random().nextString(Nicknames.length);

        return Challenger.nickName;
    }

    private Deck pickCards() {
        /* @function
         * the Challenger must choose the cards (based on the number of players) between which every player has to choose his own
         */
        return null;
    }

    // maybe we should add the possibility for every player to pick the card he wants (between the chosen one)
    private void distributeCard() {
        /* @function
         * every player chooses a card between the card picked by the Challenger
         */
    }

    private Player chooseStarter(Player Challenger) {
        /* @function
         * the Challenger decides who starts first
         */
        return Starter;
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}