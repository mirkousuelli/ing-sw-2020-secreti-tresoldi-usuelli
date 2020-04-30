package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

public class LoadGameTest {
    private final String pathFile = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_test.xml";
    @Test
    public void LoadMatchTest() throws ParserConfigurationException, SAXException {
        Game game = GameMemory.load(pathFile);

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
        assertEquals(game.getCurrentPlayer().getMalusList().get(0).isPermanent(), true);
        assertEquals(game.getCurrentPlayer().getMalusList().get(0).getNumberOfTurns(), 0);
        assertEquals(game.getCurrentPlayer().getMalusList().get(0).getDirection().get(0), MalusLevel.UP);
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).getMalusType(), MalusType.BUILD);
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).isPermanent(), false);
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).getNumberOfTurns(), 5);
        assertEquals(game.getCurrentPlayer().getMalusList().get(1).getDirection().get(0), MalusLevel.DOWN);

        /* board */
        assertSame(game.getPlayer(0).getWorkers().get(0).getLocation(), game.getBoard().getCell(1,2));
        assertSame(game.getPlayer(0).getWorkers().get(1).getLocation(), game.getBoard().getCell(4,1));
        assertSame(game.getPlayer(1).getWorkers().get(0).getLocation(), game.getBoard().getCell(0,2));
        assertSame(game.getPlayer(1).getWorkers().get(1).getLocation(), game.getBoard().getCell(4,4));
        assertSame(game.getPlayer(2).getWorkers().get(0).getLocation(), game.getBoard().getCell(0,0));
        assertSame(game.getPlayer(2).getWorkers().get(1).getLocation(), game.getBoard().getCell(0,1));
    }
}
