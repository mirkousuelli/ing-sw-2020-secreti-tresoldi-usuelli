package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface ServerConnection {

    void startServer() throws IOException;

    void connect(ServerClientHandler c, String name) throws ParserConfigurationException, SAXException;

    boolean numOfPlayers(ServerClientHandler c, Demand demand);

    //deregister
    void deregisterConnection(ServerClientHandler c);

    void SuddenDisconnection();

    boolean newGame(ServerClientHandler c, Demand demand);
}
