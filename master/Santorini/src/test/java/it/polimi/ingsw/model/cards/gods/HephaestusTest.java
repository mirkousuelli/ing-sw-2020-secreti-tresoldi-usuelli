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
import it.polimi.ingsw.model.cards.gods.exceptions.TopLevelTowerException;
import it.polimi.ingsw.model.cards.gods.exceptions.WrongCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power
 *  Your Worker may build one additional block (not dome) on top of your first block
 */

class HephaestusTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int topLevelTowerX = 0, topLevelTowerY = 1;
    static final int emptyX = 1, emptyY = 1;
    static final int otherX = 1, otherY = 0;

    static Player player1;
    static Board board;
    static Block worker1Player1, empty, topLevelTower, other;

    @BeforeAll
    static void init() throws Exception {
        player1 = new Player("Pl1");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        empty = (Block) board.getCell(emptyX, emptyY);
        topLevelTower = (Block) board.getCell(topLevelTowerX, topLevelTowerY);
        other = (Block) board.getCell(otherX, otherY);

        player1.initializeWorkerPosition(1, worker1Player1);

        worker1Player1.addPawn(player1.getWorker().get(0));

        player1.setCard(new Hephaestus());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));

        player1.build(topLevelTower);
        player1.build(topLevelTower);
        player1.build(topLevelTower);
    }

    //@Test
    //@Order(2)
    void right() throws Exception {
        player1.build(empty);
        player1.getCard().usePower(empty);

        assertEquals(Level.MIDDLE, player1.getCurrentWorker().getPreviousBuild().getLevel());
    }

    //@Test
    //@Order(1)
    void dome() {
        /*@function
         * it controls if usePower throws a TopLevelTowerException when a player is trying to build a dome on the
         * selected cell
         */
        assertThrows(TopLevelTowerException.class,
                ()->{player1.getCard().usePower(topLevelTower);} );
    }

    //@Test
    //non può se non uso cell
    void differentCell() throws Exception {
        assertThrows(WrongCellException.class,
                ()->{player1.getCard().usePower(other);} );
    }
}