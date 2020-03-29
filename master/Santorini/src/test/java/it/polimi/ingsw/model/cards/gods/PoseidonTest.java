/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*Power:
 *  If your unmoved Worker is on the ground level, it may build up to three times
 */

class PoseidonTest {

    @Test
    void TestPoseidon() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(3, 3);
        Block empty1 = (Block) board.getCell(4, 3);
        Block empty2 = (Block) board.getCell(4, 4);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);

        player1.setCard(new Poseidon());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        player1.getCard().usePower(empty1);
        player1.getCard().usePower(empty2);
        player1.getCard().usePower(empty1);

        assertEquals(Level.MIDDLE, empty1.getLevel());
        assertEquals(Level.BOTTOM, empty2.getLevel());
    }

    // it must be done in Game
    //@Test
    void testMoreThanThree() throws Exception {
        /*@function
         * it controls if usePower throws an Exception when a player tries to build more than three times with its
         * unmoved ground level worker
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty1 = (Block) board.getCell(4, 3);
        Block empty2 = (Block) board.getCell(4, 4);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Poseidon());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));





        player1.getCard().usePower(empty1);
        player1.getCard().usePower(empty2);
        player1.getCard().usePower(empty1);
        assertThrows(NullPointerException.class,
                ()->{player1.getCard().usePower(empty2);} );
    }

    @Test
    void testNotOnGroundLevel() throws Exception {
        /*@function
         * it controls if usePower throws an Exception when a player tries to use Poseidon's power even though its
         * unmoved worker is not on a ground level cell
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(1, 1);
        Block empty = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);

        player1.setCard(new Poseidon());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.build(empty);
        player1.getWorkers().get(1).moveTo(empty);





        assertThrows(NullPointerException.class,
                ()->{player1.getCard().usePower(empty);} );
    }
}