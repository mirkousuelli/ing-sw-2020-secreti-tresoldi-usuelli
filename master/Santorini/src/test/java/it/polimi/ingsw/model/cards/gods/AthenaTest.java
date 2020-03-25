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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AthenaTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int worker1Player2X = 2, worker1Player2Y = 2;

    static Player player1, player2;
    static Board board;
    static Block worker1Player1, worker1Player2, w1P1Dx, w1P2Sx;
    static List<Player> opponents;

    @BeforeAll
    static void init() throws Exception {
        player1 = new Player("Pl1");
        player2 = new Player("Pl2");
        board = new Board();
        opponents = new ArrayList<>();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        worker1Player2 = (Block) board.getCell(worker1Player2X, worker1Player2Y);
        w1P1Dx = (Block) board.getCell(worker1Player1X, worker1Player1Y + 1);
        w1P2Sx = (Block) board.getCell(worker1Player2X + 1, worker1Player2Y);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        worker1Player1.addPawn(player1.getWorker().get(0));
        worker1Player2.addPawn(player2.getWorker().get(0));

        player1.setCard(new Athena());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorker().get(0));
        player1.build(w1P1Dx);
        player1.move(w1P1Dx);

        player2.setCurrentWorker(player2.getWorker().get(0));
        opponents.add(player2);
    }

    @Test
    void testAthena() throws Exception {
        player1.getCard().usePower(opponents);
        assertTrue(player2.isCannotMoveUpActive());

        player2.build(w1P2Sx);
        player2.move(w1P2Sx);
        assertEquals(worker1Player2, player2.getCurrentWorker().getLocation());
        //assertThrows(OccupiedCellException.class,
         //       ()->{player2.move(w1P2Sx);} );
    }
}