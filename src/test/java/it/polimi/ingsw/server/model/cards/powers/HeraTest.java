package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Board;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HeraTest {
    /* Power:
     *   An opponent cannot win by moving into a perimeter space.
     */

    private Card heraCard() {
        Card heraCard = new Card();
        MalusPower heraPower = new MalusPower<>();
        Constraints constraints = heraPower.getConstraints();
        Malus malus = new Malus();

        heraPower.setEffect(Effect.MALUS); //what
        heraPower.setWorkerType(WorkerType.DEFAULT); //who
        heraPower.setWorkerInitPos(WorkerPosition.DEFAULT); //where
        heraPower.setTiming(Timing.START_TURN); //when

        constraints.setSameCell(false); //why
        constraints.setNotSameCell(false); //why
        constraints.setPerimCell(false); //why
        constraints.setNotPerimCell(true); //why
        constraints.setUnderItself(false); //why
        constraints.setNumberOfAdditional(0); //why

        heraPower.setAllowedAction(malus); //how
        malus.setMalusType(MalusType.WIN_COND); //how
        malus.setPermanent(true); //how
        malus.setNumberOfTurns(0); //how

        heraCard.addPower(heraPower);
        heraCard.setName("Hera");
        heraCard.setDescription("An opponent cannot win by moving into a perimeter space");
        heraCard.setNumPlayer(3);
        heraPower.setPersonalMalus(null);

        return heraCard;
    }

    @Test
    void HeraCorrectTest() {
        Player player1 = new Player("Pl1");
        Player player2 = new Player("Pl2");
        Board board = new Board();
        MalusPower power;

        player1.setCard(heraCard());
        power = (MalusPower) player1.getCard().getPower(0);

        Block worker1Player1 = (Block) board.getCell(1, 1);
        Block worker1Player2 = (Block) board.getCell(3, 3);
        Block topLevelPerimlCell = (Block) board.getCell(4, 4);

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        player2.initializeWorkerPosition(1, worker1Player2);
        player2.setCurrentWorker(player2.getWorkers().get(0));


        //power
        List<Player> opponents = new ArrayList<>();
        opponents.add(player2);
        assertTrue(power.usePower(opponents, player1));


        //setup top level cell
        topLevelPerimlCell.setLevel(Level.TOP);
        topLevelPerimlCell.setPreviousLevel(Level.MIDDLE);

        worker1Player2.setLevel(Level.MIDDLE);
        worker1Player2.setPreviousLevel(Level.BOTTOM);


        //opponent cannot win on a perimeter cell
        assertTrue(board.move(player2, topLevelPerimlCell)); //he cannot win but can move
        assertEquals(topLevelPerimlCell, player2.getCurrentWorker().getLocation());
        assertEquals(worker1Player2, player2.getCurrentWorker().getPreviousLocation());
        assertEquals(1, player2.getMalusList().size());
        assertEquals(power.getAllowedAction(), player2.getMalusList().get(0));
        assertEquals(MalusType.WIN_COND, MalusType.parseString("WINCOND"));
    }
}
