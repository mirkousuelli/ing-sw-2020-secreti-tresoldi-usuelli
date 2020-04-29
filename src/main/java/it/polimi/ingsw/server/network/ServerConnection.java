package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public interface ServerConnection {

    void startServer() throws IOException;

    //lobby
    void lobby(Demand demand, ServerClientHandler serverClientHandler) throws ParserConfigurationException, SAXException;

    void preLobby(ServerClientHandler serverClientHandler, String name) throws ParserConfigurationException, SAXException;

    //deregister
    void deregisterConnection(ServerClientHandler serverClientHandler);
}
