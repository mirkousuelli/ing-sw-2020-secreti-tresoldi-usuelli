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
import it.polimi.ingsw.model.exceptions.cards.CompleteTowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/*Power:
 *  Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards
 *  to an unoccupied space at any level
 */

class MinotaurTest {
    static final int worker1Player1X = 0, worker1Player1Y = 0;
    static final int worker2Player1X = 1, worker2Player1Y = 0;
    static final int worker1Player2X = 0, worker1Player2Y = 1;
    static final int worker2Player2X = 1, worker2Player2Y = 1;

    static Player player1, player2;
    static Board board;
    static Block worker1Player1, worker2Player1, worker1Player2, worker2Player2, dome, empty;

    @BeforeAll
    static void init() throws Exception {
        player1 = new Player("Pl1");
        player2 = new Player("Pl2");
        board = new Board();

        worker1Player1 = (Block) board.getCell(worker1Player1X, worker1Player1Y);
        worker2Player1 = (Block) board.getCell(worker2Player1X, worker2Player1Y);
        worker1Player2 = (Block) board.getCell(worker1Player2X, worker1Player2Y);
        worker2Player2 = (Block) board.getCell(worker2Player2X, worker2Player2Y);
        dome = (Block) board.getCell(worker1Player2X, worker1Player2Y + 1);
        empty = (Block) board.getCell(worker2Player2X + 1, worker2Player2Y + 1);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player2.initializeWorkerPosition(2, worker2Player2);

        worker1Player1.addPawn(player1.getWorkers().get(0));
        worker2Player1.addPawn(player1.getWorkers().get(1));
        worker1Player2.addPawn(player2.getWorkers().get(0));
        worker2Player2.addPawn(player2.getWorkers().get(1));

        player1.setCard(new Minotaur());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player2.setCurrentWorker(player2.getWorkers().get(0));

        player2.build(dome);
        player2.build(dome);
        player2.build(dome);
        player2.build(dome);
    }

    //@Test
    void right() throws Exception {
        player1.move(worker1Player1);
        player1.getCard().usePower(worker2Player2);

        assertEquals(empty.getPawn(), worker2Player2);
        assertEquals(worker2Player2, player1.getCurrentWorker().getLocation());
    }

    //@Test
    void noSpaceBackwards() throws Exception {
        //completeTower
        player1.move(worker1Player1);

        assertThrows(CompleteTowerException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );
    }

    //@Test
    void occupied() throws Exception {
        //Occupied
        player1.move(worker1Player1);
    }

    //@Test
    void yourWorker() throws Exception {
        player1.move(worker1Player1);

        assertThrows(WrongWorkerException.class,
                ()->{player1.getCard().usePower(worker2Player1);} );
    }
}