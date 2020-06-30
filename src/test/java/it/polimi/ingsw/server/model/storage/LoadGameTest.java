package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Level;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import static org.junit.jupiter.api.Assertions.*;

public class LoadGameTest {

    @Test
    public void LoadMatchTest() throws ParserConfigurationException, SAXException {
        String pathFile = "/xml/backup_test.xml";
        Game game = GameMemory.load(this.getClass().getResource(pathFile).toString());

        /* lobby */
        assertEquals(game.getNumPlayers(), 3);
        assertEquals(game.getPlayer(0).getNickName(), "Riccardo");
        assertEquals(game.getPlayer(0).getCard().getName(), "PERSEPHONE");
        assertEquals(game.getPlayer(1).getNickName(), "Fabio");
        assertEquals(game.getPlayer(1).getCard().getName(), "ATHENA");
        assertEquals(game.getPlayer(2).getNickName(), "Mirko");
        assertEquals(game.getPlayer(2).getCard().getName(), "ZEUS");

        /* turn */
        assertEquals(game.getCurrentPlayer().getNickName(),"Mirko");
        assertEquals(game.getState().getName(), "chooseWorker");

        /* malus */
        assertEquals(game.getCurrentPlayer().getMalusList().size(), 2);
        assertEquals(game.getCurrentPlayer().getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertTrue(game.getCurrentPlayer().getMalusList().get(0).isPermanent());
        assertEquals(game.getCurrentPlayer().getMalusList().get(0).getNumberOfTurns(), 0);
        assertEquals(game.getCurrentPlayer().getMalusList().get(0).getDirection().get(0), MalusLevel.UP);
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).getMalusType(), MalusType.BUILD);
        assertFalse(game.getCurrentPlayer().getMalusList().get(1).isPermanent());
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).getNumberOfTurns(), 5);
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).getDirection().get(0), MalusLevel.DOWN);

        /* board */
        assertSame(game.getPlayer(0).getWorkers().get(0).getLocation(), game.getBoard().getCell(1,2));
        assertSame(game.getPlayer(0).getWorkers().get(1).getLocation(), game.getBoard().getCell(4,1));
        assertSame(game.getPlayer(1).getWorkers().get(0).getLocation(), game.getBoard().getCell(0,2));
        assertSame(game.getPlayer(1).getWorkers().get(1).getLocation(), game.getBoard().getCell(4,4));
        assertSame(game.getPlayer(2).getWorkers().get(0).getLocation(), game.getBoard().getCell(0,0));
        assertSame(game.getPlayer(2).getWorkers().get(1).getLocation(), game.getBoard().getCell(0,1));

        assertEquals(game.getPlayer("Riccardo").getCurrentWorker(), game.getPlayer("Riccardo").getWorkers().get(0));
        assertEquals(game.getPlayer("Fabio").getCurrentWorker(), game.getPlayer("Fabio").getWorkers().get(0));
        assertEquals(game.getCurrentPlayer().getCurrentWorker(), game.getCurrentPlayer().getWorkers().get(1));

        assertSame(game.getPlayer(0).getWorkers().get(0), ((Block)game.getBoard().getCell(1,2)).getPawn());
        assertSame(game.getPlayer(0).getWorkers().get(1), ((Block)game.getBoard().getCell(4,1)).getPawn());
        assertSame(game.getPlayer(1).getWorkers().get(0), ((Block)game.getBoard().getCell(0,2)).getPawn());
        assertSame(game.getPlayer(1).getWorkers().get(1), ((Block)game.getBoard().getCell(4,4)).getPawn());
        assertSame(game.getPlayer(2).getWorkers().get(0), ((Block)game.getBoard().getCell(0,0)).getPawn());
        assertSame(game.getPlayer(2).getWorkers().get(1), ((Block)game.getBoard().getCell(0,1)).getPawn());

        assertEquals(game.getBoard().getCell(0,0).getLevel(), Level.GROUND);
        assertEquals(game.getBoard().getCell(0,1).getLevel(), Level.BOTTOM);
        assertEquals(game.getBoard().getCell(0,2).getLevel(), Level.MIDDLE);
        assertEquals(game.getBoard().getCell(0,3).getLevel(), Level.TOP);
        assertEquals(game.getBoard().getCell(0,4).getLevel(), Level.DOME);

        for (int i = 1; i < game.getBoard().DIM; i++)
            for (int j = 0; j < game.getBoard().DIM; j++)
                assertEquals(game.getBoard().getCell(i,j).getLevel(), Level.GROUND);
    }
}
