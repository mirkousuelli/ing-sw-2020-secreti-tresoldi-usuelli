package it.polimi.ingsw.server.network.message;

import it.polimi.ingsw.server.model.game.Game;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

public class Lobby {
    private String ID;
    private final Game game;
    private String messagePath;
    private String backupPath;

    public Lobby() throws ParserConfigurationException, SAXException {
        this.game = new Game();
    }

    public String getMessagePath() {
        return this.messagePath;
    }

    public String getBackupPath() {
        return this.backupPath;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }
}
