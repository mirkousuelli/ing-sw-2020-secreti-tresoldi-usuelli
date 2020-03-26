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
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;

/*Power:
 *  Your Worker may build one additional time, but not on the same space
 */

class DemeterTest {

    @Test
    void testDemeter() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);
        Block emptySx = (Block) board.getCell(0, 1);

        player1.initializeWorkerPosition(1, worker1Player1);


        player1.setCard(new Demeter());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.build(empty);





        player1.getCard().usePower(emptySx);

        assertEquals(Level.BOTTOM, emptySx.getLevel());
    }

    @Test
    void testSameCell() throws Exception {
        /*@function
         * it controls if usePower throws an InitialSpaceException when the selected cell is the cell where the
         * current worker was before moving
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);


        player1.setCard(new Demeter());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.build(empty);




        assertThrows(InitialCellException.class,
                ()->{player1.getCard().usePower(empty);} );
    }
}