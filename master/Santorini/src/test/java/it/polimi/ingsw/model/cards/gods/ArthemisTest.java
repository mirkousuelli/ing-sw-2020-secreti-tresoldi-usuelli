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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.cards.InitialCellException;
import it.polimi.ingsw.model.exceptions.map.OccupiedCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

/*Power:
 *  Your Worker may move one additional time, but not back to its initial space
 */

class ArthemisTest {

    @Test
    void testArthemis() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);
        Block emptySx = (Block) board.getCell(2, 1);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Arthemis());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.move(empty);




        player1.getCard().usePower(emptySx);

        assertEquals(empty, player1.getCurrentWorker().getPreviousLocation());
        assertEquals(emptySx, player1.getCurrentWorker().getLocation());
    }

    @Test
    void testInitialCell() throws Exception {
        /*@function
         * it controls if usePower throws an InitialSpaceException when the selected cell is the cell where the
         * current worker was before moving
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Arthemis());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.move(empty);




        assertThrows(InitialCellException.class,
                ()->{player1.getCard().usePower(worker1Player1);} );
    }

    @Test
    void testOccupiedCell() throws Exception {
        /*@function
         * it controls if usePower throws an OccupiedCellException when the selected cell is occupied by an other worker
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(0, 1);


        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        player1.setCard(new Arthemis());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(OccupiedCellException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );
    }

}