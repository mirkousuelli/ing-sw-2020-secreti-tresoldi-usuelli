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

import java.util.ArrayList;
import java.util.List;

/*Power:
 *  If one of your Workers moved up on your last turn, opponent Workers cannot move up this turn
 */

class AthenaTest {

    @Test
    void testAthena() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        List<Player> opponents = new ArrayList<>();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(2, 2);
        Block w1P1Dx = (Block) board.getCell(0, 1);
        Block w1P2Sx = (Block) board.getCell(3, 2);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        player1.setCard(new Athena());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.build(w1P1Dx);
        player1.move(w1P1Dx);

        player2.setCurrentWorker(player2.getWorkers().get(0));
        opponents.add(player2);




        player1.getCard().usePower(opponents);
        assertTrue(player2.isCannotMoveUpActive());

        player2.build(w1P2Sx);
        player2.move(w1P2Sx);

        assertEquals(worker1Player2, player2.getCurrentWorker().getLocation());
        //assertThrows(OccupiedCellException.class,
         //       ()->{player2.move(w1P2Sx);} );
    }
}