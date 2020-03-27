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
import it.polimi.ingsw.model.exceptions.cards.NotPerimCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

/*Power:
 *Each time your Worker moves into a perimeter space, it may immediately move again
 */

class TritonTest {

    @Test
    void testTriton() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block notPerim = (Block) board.getCell(3, 1);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Triton());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        for (int i = 0; i < 5; i++)
            player1.getCard().usePower(board.getCell(i, 0));

        assertThrows(NotPerimCellException.class,
                ()->{player1.getCard().usePower(notPerim);} );


    }

    @Test
    void testNotPerimCell() throws Exception {
        /*@function
         * it controls if usePower throws a NotPerimCellException when a worker tries to move with Triton's power on
         * a non perimeter cell
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block notPerim = (Block) board.getCell(2, 2);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Triton());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.move(notPerim);




        assertThrows(NotPerimCellException.class,
                ()->{player1.getCard().usePower(player1.getCurrentWorker().getPreviousLocation());} );
    }
}