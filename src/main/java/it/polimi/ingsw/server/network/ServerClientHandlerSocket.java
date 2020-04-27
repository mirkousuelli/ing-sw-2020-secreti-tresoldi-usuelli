package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private final Socket socket;
    private final FileXML file;
    private final ServerConnection server;
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());

    private boolean active;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server, String pathFile) throws FileNotFoundException {
        this.socket = socket;
        this.server = server;
        this.file = new FileXML(pathFile, socket);
    }

    private synchronized boolean isActive(){
        return active;
    }

    public synchronized void setActive(boolean active) {
        this.active = active;
    }

    private synchronized void send(Message message) {
        try {
            file.send(message);    // INCAPSULATO
        }
        catch(IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
        }
    }

    @Override
    public synchronized void closeConnection() {
        try {
            socket.close();
        }
        catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException, cannot close the socket", e);
        }
    }

    private synchronized void close() {
        closeConnection();
        LOGGER.info("Deregistering client...");
        server.deregisterConnection(this);
        LOGGER.info("Done");
    }

    @Override
    public void asyncSend(final Message message) {
        new Thread( () -> send(message) ).start();
    }

    private Demand read() throws IOException {
        Demand demand;

        synchronized (file.lockReceive) {
            LOGGER.info("Receiving...");
            demand = (Demand) file.receive();
        }
        LOGGER.info("Received!");

        return demand;
    }

    @Override
    public void run() {
        setActive(true);
        try {
            Demand demand = read();

            synchronized (server) {
                server.lobby(this, (String) demand.getPayload());
            }

            while(isActive()) {
                demand = read();
                notify(demand);
                LOGGER.info(LOGGER.getName() + "Notified!");
            }
        } catch (NoSuchElementException | ParserConfigurationException | SAXException | IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to receive!!" + e.getMessage(), e);
        } finally {
            setActive(false);
            close();
        }
    }
}
