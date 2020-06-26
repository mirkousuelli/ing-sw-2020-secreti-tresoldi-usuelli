package it.polimi.ingsw.server.model.game.states;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedAction;
import it.polimi.ingsw.communication.message.payload.ReducedAnswerCell;
import it.polimi.ingsw.communication.message.payload.ReducedDemandCell;
import it.polimi.ingsw.server.model.ActionToPerform;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PreparePayloadTest {

    private boolean checkReducedAnswerCell(ReducedAnswerCell rac, int x, int y, ReducedAction reducedAction) {
        return rac.getX() == x && rac.getY() == y && rac.getActionList().size() == 1 && rac.getActionList().contains(reducedAction);
    }

    private boolean checkReducedAnswerCell(ReducedAnswerCell rac, int x, int y, ReducedAction reducedAction1, ReducedAction reducedAction2) {
        return rac.getX() == x && rac.getY() == y && rac.getActionList().size() == 2 && rac.getActionList().contains(reducedAction1) && rac.getActionList().contains(reducedAction2);
    }

    @Test
    void preparePayloadMoveTestChooseWorker() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        Player p3 = new Player("Pl3");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.DEMETER);

        game.setCurrentPlayer(p3);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(2, 1);

        Block worker1Player2 = (Block) board.getCell(2, 3);
        Block worker2Player2 = (Block) board.getCell(3, 2);

        Block worker1Player3 = (Block) board.getCell(4, 3);
        Block worker2Player3 = (Block) board.getCell(3, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);


        //set current player
        game.setCurrentPlayer(p1);

        //choose worker
        p1.setCurrentWorker(p1.getWorker(1));
        game.setState(State.CHOOSE_WORKER);

        //verify prepare payload move -- state chooseWorker
        List<ReducedAnswerCell> payload = PreparePayload.preparePayloadMove(game, Timing.DEFAULT, State.CHOOSE_WORKER);

        assertEquals(4, payload.size());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 0, ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 1, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 0, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 1, ReducedAction.MOVE)).count());
    }

    @Test
    void preparePayloadMoveTestMoveNoAdditionalPower() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        Player p3 = new Player("Pl3");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.DEMETER);

        game.setCurrentPlayer(p3);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.APOLLO);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(2, 1);

        Block worker1Player2 = (Block) board.getCell(2, 3);
        Block worker2Player2 = (Block) board.getCell(3, 2);

        Block worker1Player3 = (Block) board.getCell(4, 3);
        Block worker2Player3 = (Block) board.getCell(3, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);


        //set current player
        game.setCurrentPlayer(p1);

        //move
        Block cellToMoveTo = (Block) board.getCell(1, 1);
        p1.setCurrentWorker(p1.getWorker(1));
        board.move(p1, cellToMoveTo);
        game.setState(State.MOVE);

        //verify prepare payload move -- state move, no additional power
        List<ReducedAnswerCell> payload = PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER);
        payload = PreparePayload.mergeReducedAnswerCellList(payload, PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));

        assertEquals(8, payload.size());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 0, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 1, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 0, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 1, ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 2, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 0, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 2, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 2, ReducedAction.BUILD)).count());
    }

    @Test
    void preparePayloadMoveTestMoveAdditionalPower() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        Player p3 = new Player("Pl3");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.DEMETER);

        game.setCurrentPlayer(p3);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.ARTEMIS);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(2, 1);

        Block worker1Player2 = (Block) board.getCell(2, 3);
        Block worker2Player2 = (Block) board.getCell(3, 2);

        Block worker1Player3 = (Block) board.getCell(4, 3);
        Block worker2Player3 = (Block) board.getCell(3, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);


        //set current player
        game.setCurrentPlayer(p1);

        //move
        Block cellToMoveTo = (Block) board.getCell(1, 1);
        p1.setCurrentWorker(p1.getWorker(1));
        board.move(p1, cellToMoveTo);
        game.setState(State.MOVE);

        //verify prepare payload move -- state move, additional power
        List<ReducedAnswerCell> payload = PreparePayload.preparePayloadMove(game, Timing.ADDITIONAL, State.ADDITIONAL_POWER);
        payload = PreparePayload.mergeReducedAnswerCellList(payload, PreparePayload.preparePayloadBuild(game, Timing.DEFAULT, State.MOVE));

        assertEquals(8, payload.size());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 0, ReducedAction.BUILD)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 1, ReducedAction.BUILD, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 0, ReducedAction.BUILD, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 1, ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 2, ReducedAction.BUILD, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 0, ReducedAction.BUILD, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 2, ReducedAction.BUILD, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 2, ReducedAction.BUILD, ReducedAction.USEPOWER)).count());
    }

    @Test
    void preparePayloadMoveTestMoveSurroundedCells() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        Player p3 = new Player("Pl3");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p3);
        game.assignCard(God.DEMETER);

        game.setCurrentPlayer(p1);
        game.assignCard(God.TRITON);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(4, 4);
        Block worker2Player1 = (Block) board.getCell(1, 3);

        Block worker1Player2 = (Block) board.getCell(1, 2);
        Block worker2Player2 = (Block) board.getCell(1, 4);

        Block worker1Player3 = (Block) board.getCell(0, 1);
        Block worker2Player3 = (Block) board.getCell(1, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);

        //initialize blocks' level
        board.getCell(0, 0).setLevel(Level.BOTTOM);
        ((Block) board.getCell(0, 0)).setPreviousLevel(Level.GROUND);

        board.getCell(0, 2).setLevel(Level.BOTTOM);
        ((Block) board.getCell(0, 2)).setPreviousLevel(Level.GROUND);

        board.getCell(0, 3).setLevel(Level.TOP);
        ((Block) board.getCell(0, 3)).setPreviousLevel(Level.MIDDLE);

        worker2Player1.setLevel(Level.BOTTOM);
        worker2Player1.setPreviousLevel(Level.GROUND);


        //set current player
        game.setCurrentPlayer(p1);

        //set state
        game.setState(State.CHOOSE_WORKER);

        //verify prepare payload move -- state move, additional power
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker2Player1.getX(), worker2Player1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();
        List<ReducedAnswerCell> payload = (List<ReducedAnswerCell>) returnContent.getPayload();

        assertEquals(6, payload.size());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 1, 3, ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 2, ReducedAction.MOVE, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 0, 4, ReducedAction.MOVE, ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 2, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 3, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, 2, 4, ReducedAction.MOVE, ReducedAction.USEPOWER)).count());
    }

    @Test
    void preparePayloadMoveTestMoveSurroundedWorker() throws ParserConfigurationException, SAXException {
        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Pl1");
        Player p2 = new Player("Pl2");
        Player p3 = new Player("Pl3");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.ARTEMIS);

        game.setCurrentPlayer(p3);
        game.assignCard(God.DEMETER);

        game.setCurrentPlayer(p1);
        game.assignCard(God.TRITON);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(4, 4);
        Block worker2Player1 = (Block) board.getCell(0, 0);

        Block worker1Player2 = (Block) board.getCell(1, 1);
        Block worker2Player2 = (Block) board.getCell(1, 0);

        Block worker1Player3 = (Block) board.getCell(0, 1);
        Block worker2Player3 = (Block) board.getCell(2, 1);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);


        //set current player
        game.setCurrentPlayer(p1);

        //set state
        game.setState(State.CHOOSE_WORKER);

        //verify choose worker
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker2Player1.getX(), worker2Player1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);
        ReturnContent returnContent = game.gameEngine();

        assertEquals(AnswerType.ERROR, returnContent.getAnswerType());
        assertEquals(State.CHOOSE_WORKER, returnContent.getState());
    }

    @Test
    void PreparePayloadUsePowerForPrometheus() throws ParserConfigurationException, SAXException {
        /*@function
         * it checks that if the player picked a cell under his current worker, he has to choose a different one
         */

        //set game
        Game game = new Game();
        Board board = game.getBoard();

        //set players
        Player p1 = new Player("Fabio");
        Player p2 = new Player("Mirko");
        Player p3 = new Player("Riccardo");

        //add players to the game
        game.addPlayer(p1);
        game.addPlayer(p2);
        game.addPlayer(p3);

        //assign a god everyone in the game
        game.setCurrentPlayer(p2);
        game.assignCard(God.DEMETER);

        game.setCurrentPlayer(p3);
        game.assignCard(God.ATLAS);

        game.setCurrentPlayer(p1);
        game.assignCard(God.PROMETHEUS);

        //initialize state
        Block worker1Player1 = (Block) board.getCell(1, 0);
        Block worker2Player1 = (Block) board.getCell(0, 0);

        Block worker1Player2 = (Block) board.getCell(3, 2);
        Block worker2Player2 = (Block) board.getCell(2, 2);

        Block worker1Player3 = (Block) board.getCell(0, 4);
        Block worker2Player3 = (Block) board.getCell(2, 3);

        p1.initializeWorkerPosition(1, worker1Player1);
        p1.initializeWorkerPosition(2, worker2Player1);

        p2.initializeWorkerPosition(1, worker1Player2);
        p2.initializeWorkerPosition(2, worker2Player2);

        p3.initializeWorkerPosition(1, worker1Player3);
        p3.initializeWorkerPosition(2, worker2Player3);

        //define cells where is possible to use a "use power"
        Block cellToBuildOn1 = (Block) board.getCell(1, 1);
        Block cellToBuildOn2 = (Block) board.getCell(0, 1);
        Block cellToBuildOn3 = (Block) board.getCell(2, 0);
        Block cellToBuildOn4 = (Block) board.getCell(2, 1);


        //set current player
        game.setCurrentPlayer(p1);

        //set state
        game.setState(State.CHOOSE_WORKER);

        //set cells' level
        cellToBuildOn1.setLevel(Level.BOTTOM);
        cellToBuildOn1.setPreviousLevel(Level.GROUND);

        cellToBuildOn2.setLevel(Level.MIDDLE);
        cellToBuildOn2.setPreviousLevel(Level.GROUND);


        //verify chooseWorker Prometheus cells
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.CHOOSE_WORKER, new ReducedDemandCell(worker1Player1.getX(), worker1Player1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);

        ReturnContent returnContent = game.gameEngine();
        List<ReducedAnswerCell> payload = (List<ReducedAnswerCell>) returnContent.getPayload();

        assertEquals(5, payload.size());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, worker1Player1.getX(), worker1Player1.getY(), ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn1.getX(), cellToBuildOn1.getY(), ReducedAction.USEPOWER, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn2.getX(), cellToBuildOn2.getY(), ReducedAction.USEPOWER)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn3.getX(), cellToBuildOn3.getY(), ReducedAction.USEPOWER, ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn4.getX(), cellToBuildOn4.getY(), ReducedAction.USEPOWER, ReducedAction.MOVE)).count());


        //set state
        game.setState(State.MOVE);

        //verify build -- usePower -- Prometheus cells
        game.setRequest(new ActionToPerform<>(p1.nickName, new Demand<>(DemandType.USE_POWER, new ReducedDemandCell(cellToBuildOn1.getX(), cellToBuildOn1.getY()))));
        GameMemory.save(game, Lobby.BACKUP_PATH);

        returnContent = game.gameEngine();
        payload = (List<ReducedAnswerCell>) returnContent.getPayload();

        assertEquals(4, payload.size());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, worker1Player1.getX(), worker1Player1.getY(), ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn1.getX(), cellToBuildOn1.getY(), ReducedAction.DEFAULT)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn3.getX(), cellToBuildOn3.getY(), ReducedAction.MOVE)).count());
        assertEquals(1, payload.stream().filter(rac -> checkReducedAnswerCell(rac, cellToBuildOn4.getX(), cellToBuildOn4.getY(), ReducedAction.MOVE)).count());

        assertEquals(Level.MIDDLE, cellToBuildOn1.getLevel());
        assertEquals(Level.BOTTOM, cellToBuildOn1.getPreviousLevel());
        assertEquals(cellToBuildOn1, p1.getCurrentWorker().getPreviousBuild());

        assertEquals(1, p1.getMalusList().size());
        assertEquals(MalusType.MOVE, p1.getMalusList().get(0).getMalusType());
        assertEquals(1, p1.getMalusList().get(0).getDirection().size());
        assertEquals(MalusLevel.UP, p1.getMalusList().get(0).getDirection().get(0));

    }
}
