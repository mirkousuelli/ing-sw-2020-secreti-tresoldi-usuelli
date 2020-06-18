package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testInitializeWorkerPosition() {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block worker2Player1 = (Block) board.getCell(3, 2);

        assertTrue(player1.initializeWorkerPosition(1, worker1Player1));
        assertTrue(player1.initializeWorkerPosition(2, worker2Player1));

        assertEquals(player1.getWorkers().get(0).getLocation(), worker1Player1);
        assertEquals(player1.getWorkers().get(1).getLocation(), worker2Player1);
    }

    @Test
    void testNullException() {
        /*@function
         * it controls if usePower throws a NullPointerException when the selected cell is null
         */

        Player player1 = new Player("Pl1");

        assertThrows(NullPointerException.class,
                ()-> player1.initializeWorkerPosition(1, null));
    }

    @Test
    void testWrongId() {
        /*@function
         * it controls if usePower throws a WrongWorkerException when the submitted id is not 1 or 2
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(1, 1);

        assertFalse(player1.initializeWorkerPosition(3, worker1Player1));
        assertEquals(0, player1.getWorkers().size());
    }

    @Test
    void correctResetTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it controls if the reset of the player works correctly
         */

        Player player1 = new Player("Pl1");
        Game game = new Game();

        Block worker1 = (Block) game.getBoard().getCell(1, 1);
        Worker w1 = new Worker((worker1));
        Block worker2 = (Block) game.getBoard().getCell(1, 3);
        Worker w2 = new Worker((worker2));
        Malus malus = new Malus();

        player1.addWorker(w1);
        player1.addWorker(w2);
        player1.addMalus(malus);

        // check that the workers (and the malus) are added properly
        assertEquals(w1, worker1.getPawn());
        assertEquals(w2, worker2.getPawn());
        //assertEquals(malus, player1.getMalusList());

        player1.reset();

        // check that the workers are removed from the list of player's workers
        assertNull(worker1.getPawn());
        assertNull(worker2.getPawn());

        //assertNull(player1.getMalusList());



    }

    @Test
    void removeSingleWorkerTest() throws ParserConfigurationException, SAXException {
        /*@function
         * it controls that a worker can be removed from the list of player's workers
         */

        Player player1 = new Player("Pl1");
        Game game = new Game();

        Block worker1 = (Block) game.getBoard().getCell(1, 1);
        Worker w1 = new Worker((worker1));
        Block worker2 = (Block) game.getBoard().getCell(1, 3);
        Worker w2 = new Worker((worker2));

        player1.addWorker(w1);
        player1.addWorker(w2);

        // check that the workers are added properly
        assertEquals(w1, worker1.getPawn());
        assertEquals(w2, worker2.getPawn());
        assertEquals(2, player1.getWorkers().size());

        player1.removeWorker(w2);

        // check that the second worker is removed from the list of player's workers
        assertEquals(w1, player1.getWorkers().get(0));
        assertThrows(IndexOutOfBoundsException.class,
                ()-> player1.getWorkers().get(1));

        assertNull(player1.getCard());

        // test that the number of workers for the player is decreased and now there's only one
        assertEquals(1, player1.getWorkers().size());
    }

    @Test
    void wrongAddWorker() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Game game = new Game();

        Block worker1 = (Block) game.getBoard().getCell(1, 1);
        Block worker2 = (Block) game.getBoard().getCell(1, 3);
        Block worker3 = (Block) game.getBoard().getCell(1, 4);

        player1.initializeWorkerPosition(1, worker1);
        player1.initializeWorkerPosition(2, worker2);

        assertFalse(player1.addWorker(new Worker(worker3)));
    }

    @Test
    void removeWorkers() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Game game = new Game();

        Block worker1 = (Block) game.getBoard().getCell(1, 1);
        Block worker2 = (Block) game.getBoard().getCell(1, 3);

        player1.initializeWorkerPosition(1, worker1);
        player1.initializeWorkerPosition(2, worker2);

        //remove workers
        player1.removeWorkers();

        assertTrue(player1.getWorkers().isEmpty());
    }

    @Test
    void getWrongWorkerTest() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Game game = new Game();

        Block worker1 = (Block) game.getBoard().getCell(1, 1);
        Block worker2 = (Block) game.getBoard().getCell(1, 3);

        player1.initializeWorkerPosition(1, worker1);
        player1.initializeWorkerPosition(2, worker2);

        assertNull(player1.getWorker(4));
    }

    @Test
    void removePermanentMalus() {
        Player player1 = new Player("Pl1");
        Malus permanentMalus = new Malus();
        Malus nonPermanentMalus = new Malus();

        permanentMalus.setPermanent(true);
        permanentMalus.setMalusType(MalusType.MOVE);
        permanentMalus.addDirectionElement(MalusLevel.DOWN);

        //add permanent malus
        player1.addMalus(permanentMalus);
        assertEquals(1, player1.getMalusList().size());
        assertEquals(permanentMalus, player1.getMalusList().get(0));


        nonPermanentMalus.setPermanent(false);
        nonPermanentMalus.setMalusType(MalusType.BUILD);
        nonPermanentMalus.addDirectionElement(MalusLevel.UP);

        //add non permanent malus
        player1.addMalus(nonPermanentMalus);
        assertEquals(2, player1.getMalusList().size());
        assertTrue(player1.getMalusList().contains(nonPermanentMalus));
        assertTrue(player1.getMalusList().contains(permanentMalus));

        //remove permanent malus
        assertEquals(permanentMalus, player1.removePermanentMalus());
        assertEquals(1, player1.getMalusList().size());
        assertEquals(nonPermanentMalus, player1.getMalusList().get(0));

        //remove permanent malus
        assertNull(player1.removePermanentMalus());
        assertEquals(1, player1.getMalusList().size());
        assertEquals(nonPermanentMalus, player1.getMalusList().get(0));

        assertNotEquals(permanentMalus, nonPermanentMalus);
        assertNotEquals(permanentMalus.hashCode(), nonPermanentMalus.hashCode());
    }
}