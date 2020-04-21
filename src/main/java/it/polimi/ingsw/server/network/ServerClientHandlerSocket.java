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

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private Socket socket;
    private FileXML file;
    private ServerConnection server; // ??? NON SO A COSA SERVE

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
            System.err.println(e.getMessage());
        }
    }

    @Override
    public synchronized void closeConnection() {
        try {
            socket.close();
        }
        catch (IOException e) {
            System.err.println("Error when closing socket!");
        }
        active = false;
    }

    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        //server.deregisterConnection(this);          DA FARE
        System.out.println("Done!");
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
                System.out.println("Receiving...");
                Demand read = (Demand) file.receive();
                System.out.println("Received!");
            } else {
                System.out.println("Sending...");
                send(new Answer(AnswerType.SUCCESS, DemandType.JOIN_GAME, "1234"));
                System.out.println("Sent!");
            }
        } catch (IOException | NoSuchElementException e) {
            System.err.println("Error!" + e.getMessage());
        }finally{
            close();
        }
    }
}
