package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import static org.junit.jupiter.api.Assertions.*;

public class ChangeTurnTest {

    @Test
    void correctChangePlayerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks after this turn the current player is changed correctly
         */

        Game game1 = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game1.addPlayer(p1);
        game1.addPlayer(p2);
        game1.addPlayer(p3);

        game1.setCurrentPlayer(p3);
        game1.setState(State.CHANGE_TURN);

        // it checks that the player and the state are correct before making the actions during the ChangeTurn state
        assertEquals(p3, game1.getCurrentPlayer());
        assertTrue(game1.getState() instanceof ChangeTurn);

        // it changes the current player
        game1.setRequest(new ActionToPerform(p3.nickName, new Demand(DemandType.CHANGE_TURN)));
        ReturnContent returnContent = game1.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType()); // the operation is made successfully
        assertEquals(p1, game1.getCurrentPlayer()); // the current player is changed properly


        // now check if it works in a game with 2 players
        Game game2 = new Game();
        game2.addPlayer(p1);
        game2.addPlayer(p2);

        game2.setCurrentPlayer(p1);
        game2.setState(State.CHANGE_TURN);

        // it changes the current player
        game2.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.CHANGE_TURN)));
        ReturnContent rc = game2.gameEngine();

        assertEquals(AnswerType.SUCCESS, rc.getAnswerType()); // the operation is made successfully
        assertEquals(p2, game2.getCurrentPlayer()); // the current player is changed properly
    }



    @Test
    void switchToVictoryTest() throws ParserConfigurationException, SAXException {
        /*@function
         *  it checks that if there is only one player left, the state is set to Victory
         */
        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        game.setCurrentPlayer(p1);
        game.setState(State.CHANGE_TURN);
        game.setNumPlayers(1);

        //it checks that the state is correctly set to change turn
        assertEquals("changeTurn",game.getState().getName());

        // it enters change Turn state with only one player, so it goes to victory state
        game.setRequest(new ActionToPerform(p1.nickName, new Demand(DemandType.CHANGE_TURN)));
        ReturnContent returnContent = game.gameEngine();

        // it checks that it correctly goes to victory state
        assertEquals(AnswerType.VICTORY, returnContent.getAnswerType()); // the operation is made successfully
        assertEquals(State.VICTORY, returnContent.getState());
        assertEquals(p1, game.getCurrentPlayer()); // the player is the only one remaining
    }
}