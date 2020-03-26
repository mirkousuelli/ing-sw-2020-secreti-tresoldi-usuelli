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

import it.polimi.ingsw.model.exceptions.cards.OutOfBorderException;
import it.polimi.ingsw.model.exceptions.cards.WrongCellException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.cards.CompleteTowerException;
import it.polimi.ingsw.model.exceptions.cards.WrongWorkerException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;

/*Power:
 *  Your Worker may move into an opponent Worker’s space, if their Worker can be forced one space straight backwards
 *  to an unoccupied space at any level
 */

class MinotaurTest {

    @Test
    void testMinotaur() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(2, 2);
        Block empty = (Block) board.getCell(3, 3);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        player1.setCard(new Minotaur());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player2.setCurrentWorker(player2.getWorkers().get(0));




        player1.getCard().usePower(worker1Player2);

        assertEquals(empty.getPawn(), player2.getCurrentWorker());
        assertEquals(empty, player2.getCurrentWorker().getLocation());
        assertEquals(worker1Player2, player1.getCurrentWorker().getLocation());
    }

    @Test
    void testNoSpaceBackwards() throws Exception {
        //completeTower
        /*@function
         * it controls if usePower throws a CompleteTowerException if the current player tries to force to move
         * an opponent's worker onto a dome
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(0, 3);
        Block dome = (Block) board.getCell(0, 4);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        player1.setCard(new Minotaur());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player2.setCurrentWorker(player2.getWorkers().get(0));

        player2.build(dome);
        player2.build(dome);
        player2.build(dome);
        player2.build(dome);




        assertThrows(CompleteTowerException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );
    }

    @Test
    void testOccupiedCell() throws Exception {
        //Space backwards occupied by a worker
        /*@function
         * it controls if usePower throws a Exception if the current player tries to force to move
         * a worker even if there is no space backwards!
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(2, 2);
        Block worker2Player2 = (Block) board.getCell(3, 3);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);
        player2.initializeWorkerPosition(2, worker2Player2);

        player1.setCard(new Minotaur());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(WrongCellException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );

    }

    @Test
    void testPowerAgainstYourWorker() throws Exception {
        //Power against the other worker of the currentPlayer
        /*@function
         * it controls if usePower throws a WrongWorkerException if the current player tries to force to move
         * one of his workers
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker2Player1 = (Block) board.getCell(3, 0);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.initializeWorkerPosition(2, worker2Player1);

        player1.setCard(new Minotaur());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(WrongWorkerException.class,
                ()->{player1.getCard().usePower(worker2Player1);} );
    }

    @Test
    void testPerim() throws Exception {
        //Perim worker
        /*@function
         * it controls if usePower throws a Exception if the current player tries to force to move
         * a worker out of the board
         */

        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block worker1Player2 = (Block) board.getCell(4, 4);

        player1.initializeWorkerPosition(1, worker1Player1);
        player2.initializeWorkerPosition(1, worker1Player2);

        player1.setCard(new Minotaur());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(OutOfBorderException.class,
                ()->{player1.getCard().usePower(worker1Player2);} );


    }
}