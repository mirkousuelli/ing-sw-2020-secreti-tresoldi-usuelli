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
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

/*Power:
 *  If your Worker does not move up, it may build both before and after moving
 */

class PrometheusTest {

    @Test
    void testPrometheus() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty1 = (Block) board.getCell(1, 0);
        Block tower = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Prometheus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));





        player1.getCard().usePower(empty1);
        player1.move(worker1Player1);
        player1.build(tower);

        assertTrue(player1.isCannotMoveUpActive());
    }

    @Test
    void testMoveUp() throws Exception {
        /*@function
         * it controls if the player can move up even if cannotMoveUp malus is active
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Prometheus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        player1.getCard().usePower(empty);
        //assertThrows(CannotMoveUpException.class,
        //        ()->{player1.move(empty);} );
        assertFalse(player1.getCurrentWorker().moveTo(empty));
    }
}