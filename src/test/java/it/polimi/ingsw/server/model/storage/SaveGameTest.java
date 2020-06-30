package it.polimi.ingsw.server.model.storage;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Malus;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusLevel;
import it.polimi.ingsw.server.model.cards.powers.tags.malus.MalusType;
import it.polimi.ingsw.server.model.game.Game;
import it.polimi.ingsw.server.model.game.State;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveGameTest {
    private final String pathFile = "/xml/backup_test.xml";
    private final String path = "backup_test.xml";

    @Test
    public void SaveStateTest() throws ParserConfigurationException, SAXException, IOException {
        if (!Files.exists(Paths.get(path)))
            Files.copy(Paths.get(getClass().getResource(pathFile).toString()), Paths.get(path));

        Game game_1 = GameMemory.load(path);
        Game game_2;

        game_1.setState(State.MOVE);
        GameMemory.save(game_1.getState(), path);
        game_2 = GameMemory.load(path);
        assertSame(game_1.getState().getName(), game_2.getState().getName());

        game_1.setState(State.CHOOSE_WORKER);
        GameMemory.save(game_1.getState(), path);
        game_2 = GameMemory.load(path);
        assertSame(game_2.getState().getName(), State.CHOOSE_WORKER.toString());
        assertSame(game_1.getState().getName(), game_2.getState().getName());
    }

    @Test
    public void SaveGameTest() throws ParserConfigurationException, SAXException {
        Game game = new Game();

        Player player_1 = new Player("Riccardo");
        game.addPlayer(player_1);
        game.setCurrentPlayer(player_1);
        game.assignCard(God.PERSEPHONE);
        player_1.addWorker(new Worker((Block)game.getBoard().getCell(1,2)));
        player_1.addWorker(new Worker((Block)game.getBoard().getCell(4,1)));

        Player player_2 = new Player("Fabio");
        game.addPlayer(player_2);
        game.setCurrentPlayer(player_2);
        game.assignCard(God.ATHENA);
        player_2.addWorker(new Worker((Block)game.getBoard().getCell(0,2)));
        player_2.addWorker(new Worker((Block)game.getBoard().getCell(4,4)));

        Player player_3 = new Player("Mirko");
        game.addPlayer(player_3);
        game.setCurrentPlayer(player_3);
        game.assignCard(God.ZEUS);
        player_3.addWorker(new Worker((Block)game.getBoard().getCell(0,0)));
        player_3.addWorker(new Worker((Block)game.getBoard().getCell(0,1)));
        Malus malus_13 = new Malus();
        malus_13.setPermanent(true);
        malus_13.setMalusType(MalusType.MOVE);
        malus_13.addDirectionElement(MalusLevel.UP);
        player_3.addMalus(malus_13);
        Malus malus_23 = new Malus();
        malus_23.setPermanent(false);
        malus_23.setMalusType(MalusType.BUILD);
        malus_23.setNumberOfTurns(5);
        malus_23.addDirectionElement(MalusLevel.DOWN);
        player_3.addMalus(malus_23);

        game.setCurrentPlayer(player_3);
        player_1.setCurrentWorker(player_1.getWorkers().get(0));
        player_2.setCurrentWorker(player_2.getWorkers().get(0));
        player_3.setCurrentWorker(player_3.getWorkers().get(1));
        game.setState(State.CHOOSE_WORKER);

        game.getBoard().getCell(0,1).setLevel(Level.BOTTOM);
        game.getBoard().getCell(0,2).setLevel(Level.MIDDLE);
        game.getBoard().getCell(0,3).setLevel(Level.TOP);
        game.getBoard().getCell(0,4).setLevel(Level.DOME);

        GameMemory.save(game, path);
    }

    @Test
    public void SaveBlockTest() throws ParserConfigurationException, SAXException, IOException {
        if (!Files.exists(Paths.get(path)))
            Files.copy(Paths.get(getClass().getResource(pathFile).toString()), Paths.get(path));

        Game game_1 = GameMemory.load(path);

        game_1.getBoard().getCell(0, 0).setLevel(Level.DOME);
        GameMemory.save((Block) game_1.getBoard().getCell(0, 0), path);

        Game game_2 = GameMemory.load(path);
        assertSame(game_2.getBoard().getCell(0, 0).getLevel(), Level.DOME);
        assertSame(game_2.getBoard().getCell(0, 0).getLevel(), game_1.getBoard().getCell(0, 0).getLevel());

        game_2.getBoard().getCell(0, 0).setLevel(Level.GROUND);
        GameMemory.save((Block) game_2.getBoard().getCell(0, 0), path);
    }

    @Test
    public void SaveWorkerTest() throws ParserConfigurationException, SAXException, IOException {
        if (!Files.exists(Paths.get(path)))
            Files.copy(Paths.get(getClass().getResource(pathFile).toString()), Paths.get(path));

        Game game_1 = GameMemory.load(path);

        Player player = game_1.getPlayer("Mirko");

        // for both sex
        for (int i = 0; i < 2; i++) {
            Worker worker = player.getWorkers().get(i);
            int x = worker.getLocation().getX();
            int y = worker.getLocation().getY();
            player.setCurrentWorker(player.getWorkers().get(0));
            worker.setLocation((Block) game_1.getBoard().getCell(3, 3));
            GameMemory.save(worker, player, path);

            Game game_2 = GameMemory.load(path);
            assertSame(game_2.getPlayer("Mirko").getWorkers().get(i).getLocation().getX(), 3);
            assertSame(game_2.getPlayer("Mirko").getWorkers().get(i).getLocation().getY(), 3);
            assertTrue(game_2.getBoard().getCell(x,y).isFree());
            assertFalse(game_2.getBoard().getCell(3,3).isFree());

            worker.setLocation((Block) game_1.getBoard().getCell(x,y));
            player.setCurrentWorker(player.getWorkers().get(1));
            GameMemory.save(worker, player, path);
        }
    }

    @Test
    public void SaveCurrentPlayerTest() throws ParserConfigurationException, SAXException, IOException {
        if (!Files.exists(Paths.get(path)))
            Files.copy(Paths.get(getClass().getResource(pathFile).toString()), Paths.get(path));

        Game game_1 = GameMemory.load(path);

        assertEquals("Mirko", game_1.getCurrentPlayer().getNickName());

        game_1.setCurrentPlayer(game_1.getPlayer("Fabio"));
        game_1.setState(State.MOVE);
        GameMemory.save(game_1.getCurrentPlayer(), State.MOVE, path);

        Game game_2 = GameMemory.load(path);
        assertEquals(State.MOVE.toString(), game_2.getState().getName());
        assertEquals("Fabio", game_2.getCurrentPlayer().getNickName());

        game_2.setCurrentPlayer(game_2.getPlayer("Mirko"));
        game_2.setState(State.CHOOSE_WORKER);
        GameMemory.save(game_2.getCurrentPlayer(), State.CHOOSE_WORKER, path);
        assertEquals(State.CHOOSE_WORKER.toString(), game_2.getState().getName());
        assertEquals("Mirko", game_2.getCurrentPlayer().getNickName());
    }

    @Test
    public void SavePlayerListTest() throws ParserConfigurationException, SAXException, IOException {
        if (!Files.exists(Paths.get(path)))
            Files.copy(Paths.get(getClass().getResource(pathFile).toString()), Paths.get(path));

        Game game_1 = GameMemory.load(path);
        List<Player> list = new ArrayList<>();
        Player toBeAdded = game_1.getPlayer(0);

        for (int i = 1; i < game_1.getNumPlayers(); i++) {
            list.add(game_1.getPlayer(i));
        }
        GameMemory.save(list, path);

        Game game_2 = GameMemory.load(path);
        assertEquals(game_2.getNumPlayers(), 2);
        assertEquals(game_2.getPlayer(0).getNickName(), "Fabio");
        assertEquals(game_2.getPlayer(0).getCard().getName(), "ATHENA");
        assertEquals(game_2.getPlayer(1).getNickName(), "Mirko");
        assertEquals(game_2.getPlayer(1).getCard().getName(), "ZEUS");
        assertEquals(game_2.getPlayer(1).getMalusList().size(), 2);
        assertEquals(game_2.getPlayer(1).getMalusList().get(0).getMalusType(), MalusType.MOVE);
        assertTrue(game_2.getPlayer(1).getMalusList().get(0).isPermanent());
        assertEquals(game_2.getPlayer(1).getMalusList().get(0).getNumberOfTurns(), 0);
        assertEquals(game_2.getPlayer(1).getMalusList().get(0).getDirection().get(0), MalusLevel.UP);
        assertEquals(game_2.getPlayer(1).getMalusList().get(1).getMalusType(), MalusType.BUILD);
        assertFalse(game_2.getPlayer(1).getMalusList().get(1).isPermanent());
        assertEquals(game_2.getPlayer(1).getMalusList().get(1).getNumberOfTurns(), 5);
        assertEquals(game_2.getPlayer(1).getMalusList().get(1).getDirection().get(0), MalusLevel.DOWN);


        assertSame(game_2.getPlayer(0).getWorkers().get(0).getLocation(), game_2.getBoard().getCell(0, 2));
        assertSame(game_2.getPlayer(0).getWorkers().get(1).getLocation(), game_2.getBoard().getCell(4, 4));
        assertSame(game_2.getPlayer(1).getWorkers().get(0).getLocation(), game_2.getBoard().getCell(0, 0));
        assertSame(game_2.getPlayer(1).getWorkers().get(1).getLocation(), game_2.getBoard().getCell(0, 1));

        list.add(0, toBeAdded);
        GameMemory.save(list, path);
        GameMemory.save(game_2.getPlayer("Mirko"), State.CHOOSE_WORKER, path);
    }

    @Test
    public void SavePlayerTest() throws ParserConfigurationException, SAXException, IOException {
        if (!Files.exists(Paths.get(path)))
            Files.copy(Paths.get(getClass().getResource(pathFile).toString()), Paths.get(path));

        Game game_1 = GameMemory.load(path);

        assertEquals(game_1.getCurrentPlayer().getCurrentWorker(), game_1.getPlayer("Mirko").getWorkers().get(1));
        game_1.getCurrentPlayer().setCurrentWorker(game_1.getCurrentPlayer().getWorkers().get(0));
        assertEquals(game_1.getCurrentPlayer().getCurrentWorker(), game_1.getPlayer("Mirko").getWorkers().get(0));

        GameMemory.save(game_1, path);

        Game game_2 = GameMemory.load(path);

        assertEquals(game_2.getCurrentPlayer().getCurrentWorker(), game_2.getPlayer("Mirko").getWorkers().get(0));
        game_2.getCurrentPlayer().setCurrentWorker(game_2.getCurrentPlayer().getWorkers().get(1));
        assertEquals(game_2.getCurrentPlayer().getCurrentWorker(), game_2.getPlayer("Mirko").getWorkers().get(1));

        GameMemory.save(game_2, path);
    }
}
