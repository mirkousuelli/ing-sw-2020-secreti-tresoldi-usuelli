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
import it.polimi.ingsw.model.cards.gods.exceptions.OccupiedCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power:
 *  Your Worker may build a dome at any level
 */

class AtlasTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int worker1Player2X = 1, worker1Player2Y = 0;
    static final int emptyX = 1, emptyY = 1;

    static Player player1, player2;
    static Board board;
    static Block worker1Player1, worker1Player2, empty;

    @BeforeAll
    static void init() {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        player2 = new Player("Pl2");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        worker1Player2 = (Block) board.getCell(worker1Player2X, worker1Player2Y);
        empty = (Block) board.getCell(emptyX, emptyY);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        worker1Player1.addPawn(player1.getWorker().get(0));
        worker1Player2.addPawn(player2.getWorker().get(0));

        player1.setCard(new Atlas());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));
    }

    @Test
    void testAtlas() throws Exception {
        /*@function
         * it controls if usePower functions
         */

        player1.getCard().usePower(empty);
    }

    @Test
    void testOccupiedCell() {
        /*@function
         * it controls if usePower throws an OccupiedCellException when the selected cell is occupied by an other worker
         */
        assertThrows(OccupiedCellException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );
    }
}