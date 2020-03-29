package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.exceptions.cards.WrongCellException;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/*Power
 *  Your Worker may build one additional time, but this cannot be on a perimeter space
 */

public class HestiaTest {

    @Test
    void testHestia() throws Exception {
        /*@function
         * it controls if usePower functions in the right way
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block empty = (Block) board.getCell(1, 1);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Hestia());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));
        player1.build(empty);




        player1.getCard().usePower(empty);

        assertEquals(Level.MIDDLE, player1.getCurrentWorker().getPreviousBuild().getLevel());
    }

    @Test
    void testBuildOnPerimCell() throws Exception {
        /*@function
         * it controls if usePower throws a WrongCellException if the player tries to use Hestia's power on
         * a perimeter cell
         */

        Player player1 = new Player("Pl1");
        Board board = new Board();

        Block worker1Player1 = (Block) board.getCell(0, 0);
        Block perim = (Block) board.getCell(1, 0);

        player1.initializeWorkerPosition(1, worker1Player1);

        player1.setCard(new Hestia());
        player1.getCard().setOwner(player1);

        player1.setCurrentWorker(player1.getWorkers().get(0));




        assertThrows(WrongCellException.class,
                ()->{player1.getCard().usePower(perim);} );
    }
}
