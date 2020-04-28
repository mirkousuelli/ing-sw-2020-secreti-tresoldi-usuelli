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
     * it represents the state that include all the actions to initialize the game
     */

    public Game game;
    public Player Challenger;
    public Player Starter;
    /*
    Player p1 = new Player(game.Nicknames[0]);
    Player p2 = new Player(game.Nicknames[1]);
    Player p3 = new Player(game.Nicknames[2]);
    */

    public Start(Game game) {
        /* @constructor
         * it allows the game to begin
         */

        /*this.game = game;

        // initialisation of the game
        Player challenger = PickChallenger(game);
        Deck deck = game.getDeck();
        distributeCard(game);
        Starter = chooseStarter(game);

        // it sets the current player to the Starter (the one chosen by the Challenger)
        game.setCurrentPlayer(Starter);

        // After the initialisation of the game, it goes to ChooseWorker state
        game.setState(new ChooseWorker(game));*/

    }

    private Player PickChallenger(Game game) {
        /* @function
         * it selects who is the Challenger (randomly or by picking him)
         */

       // Challenger = new Random().nextString(Nicknames.length);

        return Challenger;
    }

    private void pickCards(Game game) {
        /* @function
         * the Challenger must choose the cards (based on the number of players) between which every player has to choose his own
         */
    }

    // maybe we should add the possibility for every player to pick the card he wants (between the chosen one)
    private void distributeCard(Game game) {
        /* @function
         * every player chooses a card between the card picked by the Challenger
         */
    }

    private Player chooseStarter(Game game) {
        /* @function
         * the Challenger decides who starts first
         */
        return game.getCurrentPlayer();
    }

    @Override
    public String getName() {
        return State.START.toString();
    }

    public void gameEngine(Game game) {
        /*
         *
         */
    }
}