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
import it.polimi.ingsw.model.cards.gods.exceptions.CannotMoveUpException;
import it.polimi.ingsw.model.cards.gods.exceptions.NotPerimCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power:
 *  If your Worker does not move up, it may build both before and after moving
 */

class PrometheusTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int empty1X = 1, empty1Y = 0;
    static final int empty2X = 2, empty2Y = 0;
    static final int towerX = 1, towerY = 1;

    static Player player1;
    static Board board;
    static Block worker1Player1, empty1, empty2, tower;

    @BeforeAll
    static void init() {
        /*@function
         * it sets the objects used in the tests
         */

        player1 = new Player("Pl1");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        empty1 = (Block) board.getCell(empty1X, empty1Y);
        empty2 = (Block) board.getCell(empty2X, empty2Y);
        tower = (Block) board.getCell(towerX, towerY);

        player1.initializeWorkerPosition(1, worker1Player1);

        worker1Player1.addPawn(player1.getWorker().get(0));

        player1.setCard(new Prometheus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));
    }

    @Test
    void testPrometheus() throws Exception {
        /*@function
         * it controls if usePower functions
         */

        player1.getCard().usePower(empty1);
        player1.move(worker1Player1);
        player1.build(tower);
    }

    @Test
    void testMoveUp() throws Exception {
        /*@function
         * it controls if the player can move up even if cannotMoveUp malus is active
         */

        player1.getCard().usePower(empty1);

        assertThrows(CannotMoveUpException.class,
                ()->{player1.move(tower);} );
    }
}