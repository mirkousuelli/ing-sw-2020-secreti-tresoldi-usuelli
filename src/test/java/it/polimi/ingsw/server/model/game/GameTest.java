package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.states.Start;
import it.polimi.ingsw.server.model.game.states.Victory;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class GameTest {

    @Test
    void creationGameTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it creates a new game and checks that all the objects are created properly
         */

        Game game1 = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game1.addPlayer(p1);
        game1.addPlayer(p2);
        game1.addPlayer(p3);

        assertEquals(0, game1.getPlayerList().indexOf(p1));
        assertEquals(3, game1.getNumPlayers());
        assertEquals("Mirko",game1.getPlayer(1).getNickName());

        // it checks that the current player is the first one and then changes
        assertEquals(p1, game1.getCurrentPlayer());
        game1.setCurrentPlayer(p2);
        assertEquals(p2, game1.getCurrentPlayer());

        // it checks if it works for a 2 players game as well
        Game game2 = new Game();
        game2.addPlayer(p1);
        game2.addPlayer("Mirko");

        assertEquals(0, game2.getPlayerList().indexOf(p1));
        assertEquals(2, game2.getNumPlayers());
        assertEquals(p1 ,game2.getPlayerList().get(0));

        // it checks that the state is correctly set to Start
        assertTrue(game1.getState()instanceof Start);
    }

    @Test
    void removePlayerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it controls that a player is removed correctly
         */

        Game game = new Game();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        assertEquals(3, game.getPlayerList().size());
        assertEquals(p3, game.getPlayer(2));


        //remove the player and then checks that he is actually eliminated from the list of players in the game
        game.removePlayer("Riccardo");

        assertEquals(2, game.getPlayerList().size());

        //throws an IndexOutOfBoundsException since the player with index 2 has been removed from the game
        assertThrows(IndexOutOfBoundsException.class,
                ()-> game.getPlayer(2));
    }


    @Test
    void cleanGameTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that the game is cleaned correctly
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        List<God> chosenGods = new ArrayList<>();
        chosenGods.add(God.ARTEMIS);
        chosenGods.add(God.MINOTAUR);
        chosenGods.add(God.PERSEPHONE);

        game.getDeck().fetchDeck();
        game.setChosenGods(chosenGods);

        game.setState(State.VICTORY);
        game.setPrevState(State.BUILD);
        game.setCurrentPlayer(p2);
        game.setStarter(0);
        game.setChosenGods(chosenGods);

        Board board = game.getBoard();


        assertEquals(board, game.getBoard());
        assertTrue(game.getState()instanceof Victory);
        assertEquals(State.BUILD,game.getPrevState());
        assertEquals(3, game.getChosenGods().size());
        assertEquals(God.ARTEMIS, game.getChosenGods().get(0).getGod());
        assertEquals(p2, game.getCurrentPlayer());
        assertEquals(0,game.getStarter());

        //cleans the game and then check that all the pieces of information are reset
        game.clean();

        assertEquals(board, game.getBoard());
        assertEquals(State.START, game.getPrevState());
        assertEquals(0, game.getChosenGods().size());
        assertEquals(p1, game.getCurrentPlayer());
        assertEquals(-1,game.getStarter());
    }
}