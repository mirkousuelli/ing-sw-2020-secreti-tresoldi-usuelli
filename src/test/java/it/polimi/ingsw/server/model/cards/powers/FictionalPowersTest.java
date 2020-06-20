package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.MovementType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FictionalPowersTest {

    private Card fictional1Card() {
        Card fictional1Card = new Card();
        BuildPower fictional1Power = new BuildPower<>();
        Constraints constraints = fictional1Power.getConstraints();

        fictional1Power.setEffect(Effect.BUILD); //what
        fictional1Power.setWorkerType(WorkerType.DEFAULT); //who
        fictional1Power.setWorkerInitPos(WorkerPosition.MIDDLE); //where
        fictional1Power.setTiming(Timing.ADDITIONAL); //when
        constraints.setSameCell(false); //why
        constraints.setNotSameCell(false); //why
        constraints.setPerimCell(false); //why
        constraints.setNotPerimCell(false); //why
        constraints.setUnderItself(false); //why
        constraints.setNumberOfAdditional(1); //why
        fictional1Power.setAllowedAction(BlockType.NOT_DOME); //how

        fictional1Card.addPower(fictional1Power);
        fictional1Card.setName("Fictional Additional-Middle-NOT_DOME");
        fictional1Card.setDescription("Fictional1");
        fictional1Card.setNumPlayer(3);
        fictional1Power.setPersonalMalus(null);

        return fictional1Card;
    }

    @Test
    void fictional1CorrectTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(fictional1Card());
        power = (BuildPower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block buildPowerCell = (Block) board.getCell(0, 0);
        Block cellToBuildUp = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //set middle level to the current worker location
        worker1Player1.setLevel(Level.MIDDLE);
        worker1Player1.setPreviousLevel(Level.BOTTOM);

        //basic build
        assertTrue(board.build(player1, cellToBuildUp));
        assertEquals(Level.GROUND, cellToBuildUp.getPreviousLevel());
        assertEquals(Level.BOTTOM, cellToBuildUp.getLevel());

        //additional build with power and the middle-level currentWorker and a non dome block
        assertTrue(power.usePower(player1, buildPowerCell, board.getAround(buildPowerCell)));
        assertEquals(Level.GROUND, buildPowerCell.getPreviousLevel());
        assertEquals(Level.BOTTOM, buildPowerCell.getLevel());
        assertEquals(0, power.getNumberOfActionsRemaining());
        assertEquals(WorkerPosition.parseString("MIDDLE"), power.workerInitPos);
    }

    @Test
    void fictional1WrongLevelTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(fictional1Card());
        power = (BuildPower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block buildPowerCell = (Block) board.getCell(0, 0);
        Block cellToBuildUp = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //basic build
        assertTrue(board.build(player1, cellToBuildUp));
        assertEquals(Level.GROUND, cellToBuildUp.getPreviousLevel());
        assertEquals(Level.BOTTOM, cellToBuildUp.getLevel());

        //additional build with power and NOT the middle-level currentWorker and a non dome block
        assertFalse(power.usePower(player1, buildPowerCell, board.getAround(buildPowerCell)));
        assertEquals(Level.GROUND, buildPowerCell.getPreviousLevel());
        assertEquals(Level.GROUND, buildPowerCell.getLevel());
        assertEquals(power.getConstraints().getNumberOfAdditional(), power.getNumberOfActionsRemaining());
    }

    @Test
    void fictional1WrongBlockTypeTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        BuildPower power;

        player1.setCard(fictional1Card());
        power = (BuildPower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block buildPowerCell = (Block) board.getCell(0, 0);
        Block cellToBuildUp = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //set middle level to the current worker location
        worker1Player1.setLevel(Level.MIDDLE);
        worker1Player1.setPreviousLevel(Level.BOTTOM);

        //set top level to buildPowerCell
        buildPowerCell.setLevel(Level.TOP);
        buildPowerCell.setPreviousLevel(Level.MIDDLE);

        //basic build
        assertTrue(board.build(player1, cellToBuildUp));
        assertEquals(Level.GROUND, cellToBuildUp.getPreviousLevel());
        assertEquals(Level.BOTTOM, cellToBuildUp.getLevel());

        //additional build with power and the middle-level currentWorker and A dome
        assertFalse(power.usePower(player1, buildPowerCell, board.getAround(buildPowerCell)));
        assertEquals(Level.MIDDLE, buildPowerCell.getPreviousLevel());
        assertEquals(Level.TOP, buildPowerCell.getLevel());
        assertEquals(power.getConstraints().getNumberOfAdditional(), power.getNumberOfActionsRemaining());
    }


    private Card fictional2Card() {
        Card fictional2Card = new Card();
        MovePower fictional2Power = new MovePower<>();
        Constraints constraints = fictional2Power.getConstraints();

        fictional2Power.setEffect(Effect.MOVE); //what
        fictional2Power.setWorkerType(WorkerType.UNMOVED_WORKER); //who
        fictional2Power.setWorkerInitPos(WorkerPosition.BOTTOM); //where
        fictional2Power.setTiming(Timing.ADDITIONAL); //when
        constraints.setSameCell(false); //why
        constraints.setNotSameCell(false); //why
        constraints.setPerimCell(false); //why
        constraints.setNotPerimCell(true); //why
        constraints.setUnderItself(false); //why
        constraints.setNumberOfAdditional(2); //why
        fictional2Power.setAllowedAction(MovementType.DEFAULT); //how

        fictional2Card.addPower(fictional2Power);
        fictional2Card.setName("Fictional Additional-Bottom-Default");
        fictional2Card.setDescription("Fictional2");
        fictional2Card.setNumPlayer(3);
        fictional2Power.setPersonalMalus(null);

        return fictional2Card;
    }

    @Test
    void fictional2CorrectTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power;

        player1.setCard(fictional2Card());
        power = (MovePower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(3, 3);

        Block movePowerCell1 = (Block) board.getCell(2, 2);
        Block movePowerCell2 = (Block) board.getCell(2, 3);
        Block movePowerCell3 = (Block) board.getCell(1, 3);


        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorker(1));

        Worker unmovedWorker = player1.getWorker(2);

        //set bottom level to the current worker location
        worker2Player1.setLevel(Level.BOTTOM);
        movePowerCell1.setLevel(Level.BOTTOM);
        movePowerCell2.setLevel(Level.BOTTOM);
        movePowerCell3.setLevel(Level.BOTTOM);


        //first additional move with power and the bottom-level unmoved worker on a not perim cell
        assertTrue(power.usePower(player1, movePowerCell1, board.getAround(movePowerCell1)));
        assertEquals(worker2Player1, unmovedWorker.getPreviousLocation());
        assertEquals(movePowerCell1, unmovedWorker.getLocation());
        assertEquals(power.getConstraints().getNumberOfAdditional() - 1, power.getNumberOfActionsRemaining());
        assertEquals(WorkerPosition.parseString("BOTTOM"), power.workerInitPos);
        assertEquals(WorkerType.parseString("UNMOVED"), power.workerType);

        //second additional move with power and the bottom-level unmoved worker on a not perim cell
        assertTrue(power.usePower(player1, movePowerCell2, board.getAround(movePowerCell2)));
        assertEquals(movePowerCell1, unmovedWorker.getPreviousLocation());
        assertEquals(movePowerCell2, unmovedWorker.getLocation());
        assertEquals(0, power.getNumberOfActionsRemaining());


        //third additional move with power and the bottom-level unmoved worker on a not perim cell
        assertFalse(power.usePower(player1, movePowerCell3, board.getAround(movePowerCell3)));
        assertEquals(movePowerCell1, unmovedWorker.getPreviousLocation());
        assertEquals(movePowerCell2, unmovedWorker.getLocation());
        assertEquals(0, power.getNumberOfActionsRemaining());
    }

    @Test
    void fictional2WrongLevelTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power;

        player1.setCard(fictional2Card());
        power = (MovePower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(3, 3);

        Block movePowerCell1 = (Block) board.getCell(2, 2);


        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorker(1));

        Worker unmovedWorker = player1.getWorker(2);

        //set top level to the current worker location
        worker2Player1.setLevel(Level.TOP);


        //first additional move with power and NOT the bottom-level unmoved worker on a not perim cell
        assertFalse(power.usePower(player1, movePowerCell1, board.getAround(movePowerCell1)));
        assertEquals(worker2Player1, unmovedWorker.getPreviousLocation());
        assertEquals(worker2Player1, unmovedWorker.getLocation());
        assertEquals(power.getConstraints().getNumberOfAdditional(), power.getNumberOfActionsRemaining());
    }

    @Test
    void fictional2WrongWorkerTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power;

        player1.setCard(fictional2Card());
        power = (MovePower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(3, 3);

        Block movePowerCell1 = (Block) board.getCell(2, 2);


        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorker(2));

        Worker movedWorker = player1.getWorker(2);

        //set bottom level to the current worker location
        worker2Player1.setLevel(Level.BOTTOM);


        //first additional move with power and the bottom-level CURRENT worker on a not perim cell
        assertFalse(power.usePower(player1, movePowerCell1, board.getAround(movePowerCell1)));
        assertEquals(worker2Player1, movedWorker.getPreviousLocation());
        assertEquals(worker2Player1, movedWorker.getLocation());
        assertEquals(power.getConstraints().getNumberOfAdditional(), power.getNumberOfActionsRemaining());
    }

    @Test
    void fictional2WrongCellTest() {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        MovePower power;

        player1.setCard(fictional2Card());
        power = (MovePower) player1.getCard().getPower(0);
        power.setNumberOfActionsRemaining();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(3, 3);

        Block movePowerCell1 = (Block) board.getCell(4, 4);


        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player1.setCurrentWorker(player1.getWorker(2));

        Worker unmovedWorker = player1.getWorker(2);

        //set bottom level to the current worker location
        worker2Player1.setLevel(Level.BOTTOM);


        //first additional move with power and the bottom-level unmoved worker on a PERIM cell
        assertFalse(power.usePower(player1, movePowerCell1, board.getAround(movePowerCell1)));
        assertEquals(worker2Player1, unmovedWorker.getPreviousLocation());
        assertEquals(worker2Player1, unmovedWorker.getLocation());
        assertEquals(power.getConstraints().getNumberOfAdditional(), power.getNumberOfActionsRemaining());
    }
}
