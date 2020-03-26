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
import it.polimi.ingsw.model.exceptions.cards.TopLevelTowerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;

import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power:
 *Your Worker may build a block under itself
 */

class ZeusTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int towerX = 1, towerY = 0;

    static Player player1;
    static Board board;
    static Block worker1Player1, tower;

    @BeforeAll
    static void init() throws Exception {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        tower = (Block) board.getCell(towerX, towerY);

        player1.initializeWorkerPosition(1, worker1Player1);

        worker1Player1.addPawn(player1.getWorkers().get(0));

        player1.setCard(new Zeus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));

        player1.move(worker1Player1);
        player1.build(tower);
        player1.build(tower);
        player1.build(tower);
    }

    //@Test
    //@Order(1)
    void testZeus() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        //assertEquals(worker1Player1, player1.getCurrentWorker().getLocation());
        //assertEquals(worker1Player1.getLevel(), player1.getCurrentWorker().getLocation().getLevel());
        //assertEquals(Level.GROUND, worker1Player1.getLevel());
        //assertEquals(Level.TOP, tower.getLevel());

        assertEquals(Level.GROUND, player1.getCurrentWorker().getLocation().getLevel());

        player1.getCard().usePower(worker1Player1);
        assertEquals(Level.BOTTOM, player1.getCurrentWorker().getLocation().getLevel());

        player1.getCard().usePower(worker1Player1);
        assertEquals(Level.MIDDLE, player1.getCurrentWorker().getLocation().getLevel());

        player1.getCard().usePower(worker1Player1);
        assertEquals(Level.TOP, player1.getCurrentWorker().getLocation().getLevel());
    }

    //@Test
    //@Order(2)
    void testBuildDome() throws Exception {
        /*@function
         * it controls if usePower throws a TopLevelTowerException when a worker tries to build a dome under
         * itself with Zeus' power
         */

        player1.move(tower);

        assertThrows(TopLevelTowerException.class,
                ()->{player1.getCard().usePower(tower);} );
    }
}