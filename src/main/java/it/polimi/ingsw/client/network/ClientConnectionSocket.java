package it.polimi.ingsw.client.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientConnectionSocket<S> extends Observable<Answer<S>> implements ClientConnection<S> {
    Socket socket;
    FileXML file;

    private final String FILE = "src/main/java/it/polimi/ingsw/client/network/message/message.xml"; // X TESTING
    private static final Logger LOGGER = Logger.getLogger(ClientConnectionSocket.class.getName());
    private boolean isActive;

    public ClientConnectionSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        isActive = true;
    }

    public void startClient() throws IOException {
        boolean testDemand = true; // DA CAMBIARE anche in ServerClientHandlerSocket !!!!!!!!!!!!!
        file = new FileXML(FILE, socket);

        LOGGER.info("Connection established");

        try {
            //while (true){        L'HO TOLTO PER TESTING
            if (testDemand) {
                LOGGER.info("Sending...");
                file.send(new Demand(DemandType.JOIN_GAME, "1234"));
                LOGGER.info("Sent!");

            } else {
                LOGGER.info("Receiving...");
                Answer answer = (Answer) file.receive();
                LOGGER.info("Received!");
            }
            //}
        } catch(NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Connection closed.", e);
        }
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void update(Demand<S> demand) {
        try {
            file.send(demand);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException!", e);
        }

        LOGGER.info("Sent!");
    }

    @Override
    public void closeConnection() {
        try {
            socket.close();
            isActive = false;
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot close the socket", e);
        }
    }
}