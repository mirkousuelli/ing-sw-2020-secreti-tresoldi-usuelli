package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.WinType;
import it.polimi.ingsw.model.map.Block;
import it.polimi.ingsw.model.map.Board;
import it.polimi.ingsw.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.List;

public class ChronusTest {
    /* Power:
     *   You also win if your Worker moves down two or more levels
     */

    @Test
    void testChronus() throws ParserConfigurationException, SAXException {
        Player player1 = new Player("Pl1");
        Board board = new Board();
        Deck deck = new Deck();
        WinConditionPower power1;

        deck.fetchCard(God.CHRONUS);
        player1.setCard(deck.popRandomCard());
        power1 = (WinConditionPower) player1.getCard().getPower(0);
        //power1 = new WinConditionPower();
        //player1.getCard().addPower(power1);

        Block worker1Player1 = (Block) board.getCell(1,1);
        List<Block> towerList = new ArrayList<>();

        player1.initializeWorkerPosition(1, worker1Player1);
        player1.setCurrentWorker(player1.getWorkers().get(0));

        //Chronus
        /*power1.setWorkerType(WorkerType.DEFAULT);
        power1.setWorkerInitPos(WorkerPosition.DEFAULT);
        power1.setEffect(Effect.WIN_COND);
        power1.setTiming(Timing.DEFAULT);
        power1.getConstraints().setNumberOfAdditional(0);
        power1.getConstraints().setNotPerimCell(false);
        power1.getConstraints().setNotSameCell(false);
        power1.getConstraints().setPerimCell(false);
        power1.getConstraints().setSameCell(false);
        power1.getConstraints().setUnderItself(false);
        power1.setAllowedBlock(BlockType.DEFAULT);
        power1.setAllowedMove(MovementType.DEFAULT);
        power1.setAllowedWin(WinType.FIVETOWER);*/

        for (int i = 0; i < 5; i++) {
            Block tower = (Block) board.getCell(i, 0);
            tower.setLevel(Level.DOME);
            tower.setPreviousLevel(Level.TOP);

            towerList.add(tower);
        }

        //win condition power
        //assertTrue(power1.usePower(board));
    }
}
