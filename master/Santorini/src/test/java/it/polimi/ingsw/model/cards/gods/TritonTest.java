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
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.cards.NotPerimCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Cell;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;

import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power:
 *Each time your Worker moves into a perimeter space, it may immediately move again
 */

class TritonTest {
    static final int worker1Player1X = 1, worker1Player1Y = 1;
    static final int perimX = 0, perimY = 0;
    static final int notPerimX = 3, notPerimY = 3;

    static Player player1;
    static Board board;
    static Block worker1Player1, perim, notPerim;

    @BeforeAll
    static void init() throws Exception {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        perim = (Block) board.getCell(perimX, perimY);
        notPerim = (Block) board.getCell(notPerimX, notPerimY);

        player1.initializeWorkerPosition(1, worker1Player1);

        worker1Player1.addPawn(player1.getWorkers().get(0));

        player1.setCard(new Triton());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));

        player1.move(worker1Player1);
    }

    @Test
    @Order(1)
    void testTriton() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Cell cell;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j <5; j++) {
                cell = board.getCell(perimX + i, perimY + j);
                if (cell.getX() == 0 || cell.getY() == 0 || cell.getX() == 4 || cell.getY() == 4)
                    player1.getCard().usePower(cell);
            }
        }
    }

    @Test
    @Order(2)
    void testNotPerimCell() throws Exception {
        /*@function
         * it controls if usePower throws a NotPerimCellException when a worker tries to move with Triton's power on
         * a non perimeter cell
         */

        player1.move(notPerim);

        assertThrows(NotPerimCellException.class,
                ()->{player1.getCard().usePower(player1.getCurrentWorker().getPreviousLocation());} );
    }
}