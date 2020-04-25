package it.polimi.ingsw.client.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
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
                LOGGER.log(Level.INFO, "Sending...");
                file.send(new Demand(DemandType.JOIN_GAME, "1234"));
                LOGGER.log(Level.INFO, "Sent!");

            } else {
                LOGGER.log(Level.INFO, "Receiving...");
                Answer answer = (Answer) file.receive();
                LOGGER.log(Level.INFO, "Received!");
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
        LOGGER.log(Level.INFO, "Sent!");
        try {
            file.send(demand);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException!", e);
        }
    }

    @Override
    public void closeConnection() {
        try {
            socket.close();
            isActive = false;
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, cannot close the socket", e);
        }
    }
}