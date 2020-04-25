package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.header.AnswerType;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private Socket socket;
    private FileXML file;
    private ServerConnection server; // ??? NON SO A COSA SERVE
    private static final Logger LOGGER = Logger.getLogger(ServerClientHandlerSocket.class.getName());

    private boolean active = true;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server, String pathFile) throws FileNotFoundException {
        this.socket = socket;
        this.server = server; // ??? NON SO A COSA SERVE
        this.file = new FileXML(pathFile, socket);
    }

    private synchronized boolean isActive(){
        return active;
    }

    private synchronized void send(Message message) {
        try {
            this.file.send(message);    // INCAPSULATO
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
        active = false;
    }

    private void close() {
        closeConnection();
        LOGGER.info("Deregistering client...");
        //server.deregisterConnection(this);          DA FARE
        LOGGER.info("Done");
    }

    @Override
    public void asyncSend(final Object message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                send((Message) message);
            }
        }).start();
    }

    @Override
    public void run() {
        boolean testDemand = false; // DA CAMBIARE anche in ClientConnectionSocket !!!!!!!!!!!!!

        try{
            if (testDemand) {
                LOGGER.info("Receiving...");
                Demand read = (Demand) file.receive();
                LOGGER.info("Received!");
            } else {
                LOGGER.info("Sending...");
                send(new Answer(AnswerType.SUCCESS, DemandType.JOIN_GAME, "1234"));
                LOGGER.info("Sent!");
            }
        } catch (IOException | NoSuchElementException e) {
            LOGGER.log(Level.SEVERE, "Error!" + e.getMessage(), e);
        }finally{
            close();
        }
    }
}
