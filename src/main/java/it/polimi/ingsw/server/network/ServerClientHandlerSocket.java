package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private final Socket socket;
    private final FileXML file;
    private ServerConnection server; // ??? NON SO A COSA SERVE
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());

    private boolean active;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server, String pathFile) throws FileNotFoundException {
        this.socket = socket;
        this.server = server; // ??? NON SO A COSA SERVE
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
        //server.deregisterConnection(this);          DA FARE
        LOGGER.info("Done");
    }

    @Override
    public void asyncSend(final Message message) {
        new Thread(
                () -> {
                    send(message);
                    }
                ).start();
    }

    @Override
    public void run() {
        setActive(true);
        try {
            Demand demand;
            while(isActive()) {
                synchronized (file) {
                    while (!file.isChangedServer()) file.wait();

                    LOGGER.info("Receiving...");
                    demand = (Demand) file.receive();
                }

                LOGGER.info("Received!");
                LOGGER.info(LOGGER.getName() + "Notify!");
                //notify(demand);
            }
        } catch (IOException | NoSuchElementException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Error!" + e.getMessage(), e);
        } finally {
            setActive(false);
            close();
        }
    }
}
