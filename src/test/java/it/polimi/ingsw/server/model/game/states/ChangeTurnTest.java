package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.ReturnContent;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.storage.GameMemory;
import it.polimi.ingsw.server.network.Lobby;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ChangeTurnTest {

    @Test
    void correctChangePlayerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks after this turn the current player is changed correctly
         */

        Lobby lobby = new Lobby(new Game());
        Game game1 = lobby.getGame();
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
        //game1.setRequest(new ActionToPerform<>(p3.nickName, new Demand<>(DemandType.CHANGE_TURN)));
        ReturnContent returnContent = game1.gameEngine();

        assertEquals(AnswerType.CHANGE_TURN, returnContent.getAnswerType()); // the operation is made successfully
        assertEquals(p1, game1.getCurrentPlayer()); // the current player is changed properly


        // now check if it works in a game with 2 players
        lobby = new Lobby(new Game());
        Game game2 = lobby.getGame();
        game2.addPlayer(p1);
        game2.addPlayer(p2);

        game2.setCurrentPlayer(p1);
        game2.setState(State.CHANGE_TURN);

        // it changes the current player
        //game2.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHANGE_TURN)));
        ReturnContent rc = game2.gameEngine();

        assertEquals(AnswerType.CHANGE_TURN, rc.getAnswerType()); // the operation is made successfully
        assertEquals(p2, game2.getCurrentPlayer()); // the current player is changed properly
    }

    @Test
    void switchToVictoryTest() throws ParserConfigurationException, SAXException {
        /*@function
         *  it checks that if there is only one player left, the state is set to Victory
         */

        Lobby lobby = new Lobby(new Game());
        Game game = lobby.getGame();
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
        assertEquals("changeTurn", game.getState().getName());

        // it enters change Turn state with only one player, so it goes to victory state
        //game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHANGE_TURN)));
        ReturnContent returnContent = game.gameEngine();

        // it checks that it correctly goes to victory state
        assertEquals(AnswerType.VICTORY, returnContent.getAnswerType()); // the operation is made successfully
        assertEquals(State.VICTORY, returnContent.getState());
        assertEquals(p1, game.getCurrentPlayer()); // the player is the only one remaining
    }

    @Test
    void removeMalusTest() throws ParserConfigurationException, SAXException, IOException {
        /*@function
         * it checks that a non permanent malus is removed when it has expired
         */

        Game game = new Game();

        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        Board board = game.getBoard();
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block worker1Player2 = (Block) board.getCell(4, 0);
        Block worker1Player3 = (Block) board.getCell(4, 4);
        Block cellToBuildOn = (Block) board.getCell(1, 1);
        Block cellToBuildOn1 = (Block) board.getCell(0, 2);
        Block cellToMoveTo = (Block) board.getCell(0, 1);

        game.setCurrentPlayer(p2);
        game.assignCard(God.DEMETER);
        game.setCurrentPlayer(p3);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        p1.initializeWorkerPosition(1, worker1Player1);
        p1.setCurrentWorker(p1.getWorkers().get(0));
        game.assignCard(God.PROMETHEUS);

        p2.initializeWorkerPosition(1, worker1Player2);
        p3.initializeWorkerPosition(1, worker1Player3);

        Files.deleteIfExists(Paths.get(Lobby.BACKUP_PATH));
        game.setState(State.CHOOSE_WORKER);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(1, 0))));
        game.gameEngine();

        //usePower that adds a personal malus
        game.setState(State.MOVE);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(cellToBuildOn.getX(), cellToBuildOn.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToBuildOn.getLevel());
        assertEquals(State.MOVE, returnContent.getState());
        assertEquals(1, p1.getMalusList().size());
        assertEquals(p1.getCard().getPower(0).getPersonalMalus(), p1.getMalusList().get(0));
        assertEquals(p1, game.getCurrentPlayer());

        //move blocked by the newly added malus
        game.setState(State.MOVE);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(cellToBuildOn.getX(), cellToBuildOn.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(worker1Player1.getPawn(), p1.getCurrentWorker());
        assertEquals(State.MOVE, returnContent.getState());
        assertEquals(p1, game.getCurrentPlayer());

        //move correct
        game.setState(State.MOVE);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(cellToMoveTo.getX(), cellToMoveTo.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.GROUND, cellToMoveTo.getLevel());
        assertEquals(cellToMoveTo.getPawn(), p1.getCurrentWorker());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(p1, game.getCurrentPlayer());

        //build correct
        game.setState(State.BUILD);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.BUILD, new ReducedDemandCell(cellToBuildOn1.getX(), cellToBuildOn1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToBuildOn1.getLevel());
        assertTrue(returnContent.isChangeTurn());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
        assertTrue(p1.getMalusList().isEmpty());
        assertEquals(p2, game.getCurrentPlayer());


        //new turn
        Block cellToBuildOn2 = (Block) board.getCell(1, 2);
        Block cellToMoveTo2 = (Block) board.getCell(1, 1);
        game.setCurrentPlayer(p1);

        Files.deleteIfExists(Paths.get(Lobby.BACKUP_PATH));
        game.setState(State.CHOOSE_WORKER);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(0, 1))));
        game.gameEngine();

        //move up correct (malus has expired)
        game.setState(State.MOVE);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.MOVE, new ReducedDemandCell(cellToMoveTo2.getX(), cellToMoveTo2.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToMoveTo2.getLevel());
        assertEquals(cellToMoveTo2.getPawn(), p1.getCurrentWorker());
        assertEquals(State.BUILD, returnContent.getState());
        assertEquals(p1, game.getCurrentPlayer());

        //build correct
        game.setState(State.BUILD);
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.BUILD, new ReducedDemandCell(cellToBuildOn2.getX(), cellToBuildOn2.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        returnContent = game.gameEngine();

        assertEquals(AnswerType.SUCCESS, returnContent.getAnswerType());
        assertEquals(Level.BOTTOM, cellToBuildOn2.getLevel());
        assertTrue(returnContent.isChangeTurn());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
        assertTrue(p1.getMalusList().isEmpty());
        assertEquals(p2, game.getCurrentPlayer());
    }
}