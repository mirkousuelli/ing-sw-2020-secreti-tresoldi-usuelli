package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

public class SaveGameTest {
    private final String pathFile = "src/main/java/it/polimi/ingsw/server/model/storage/xml/backup_test.xml";

    @Test
    public void SaveStateTest() throws ParserConfigurationException, SAXException {
        Game game_1 = GameMemory.load(pathFile);
        Game game_2;

        game_1.setState(State.MOVE);
        GameMemory.save(game_1.getState(), pathFile);
        game_2 = GameMemory.load(pathFile);
        assertSame(game_1.getState().getName(), game_2.getState().getName());

        game_1.setState(State.CHOOSE_WORKER);
        GameMemory.save(game_1.getState(), pathFile);
        game_2 = GameMemory.load(pathFile);
        assertSame(game_2.getState().getName(), State.CHOOSE_WORKER.toString());
        assertSame(game_1.getState().getName(), game_2.getState().getName());
    }
}
