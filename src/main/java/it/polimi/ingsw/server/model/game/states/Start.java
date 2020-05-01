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
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Deck;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.GameState;
import it.polimi.ingsw.server.model.game.State;

public class Start implements GameState {
    /* @abstractClass
     * it represents the state that includes all the actions to initialize the game
     */

    public Game game;
    public Player Challenger;
    public Player Starter;

    // TODO complete the missing actions to start the game
    public Start(Game game) {
        /* @constructor
         * it sets the game which the state is connected to
         */

        this.game = game;
    }

    private void pickCards(Game game) {
        /* @function
         * the Challenger must choose the cards (based on the number of players) among which every player has to choose his own
         */

    }

    private void distributeCard(Game game) {
        /* @function
         * every player chooses a card between the card picked by the Challenger
         */
    }

    private Player pickStarter() {
        /* @function
         * the Challenger decides who starts first
         */
        return game.getCurrentPlayer();
    }

    @Override
    public String getName() {
        return State.START.toString();
    }

    @Override
    public State gameEngine(Game game) {

        // initialisation of the game
        Challenger = game.getPlayerList().get(0);
        Deck deck = game.getDeck();
        distributeCard(game);
        Starter = pickStarter();

        // it sets the current player to the Starter (the one chosen by the Challenger)
        game.setCurrentPlayer(Starter);

        // After the initialisation of the game, it goes to ChooseWorker state
        return State.CHOOSE_WORKER;
    }
}