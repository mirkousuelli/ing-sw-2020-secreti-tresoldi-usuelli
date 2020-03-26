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
import it.polimi.ingsw.model.exceptions.cards.TopLevelTowerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;

/*Power:
 *Your Worker may build a block under itself
 */

class ZeusTest {

    @Test
    void testZeus() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Zeus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        player1.getCard().usePower(worker1Player1);
        assertEquals(Level.BOTTOM, player1.getCurrentWorker().getLocation().getLevel());

        player1.getCard().usePower(worker1Player1);
        assertEquals(Level.MIDDLE, player1.getCurrentWorker().getLocation().getLevel());

        player1.getCard().usePower(worker1Player1);
        assertEquals(Level.TOP, player1.getCurrentWorker().getLocation().getLevel());
    }

    @Test
    void testBuildDome() throws Exception {
        /*@function
         * it controls if usePower throws a TopLevelTowerException when a worker tries to build a dome under
         * itself with Zeus' power
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Zeus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));;
        player1.getCard().usePower(worker1Player1);
        player1.getCard().usePower(worker1Player1);
        player1.getCard().usePower(worker1Player1);




        assertThrows(TopLevelTowerException.class,
                ()->{player1.getCard().usePower(worker1Player1);} );
    }
}