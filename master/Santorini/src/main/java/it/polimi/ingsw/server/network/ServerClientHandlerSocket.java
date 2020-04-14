package it.polimi.ingsw.server.network;

import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.InputStreamXML;
import it.polimi.ingsw.communication.message.xml.network.OutputStreamXML;
import it.polimi.ingsw.communication.message.xml.network.SenderXML;
import it.polimi.ingsw.communication.observer.Observable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ServerClientHandlerSocket extends Observable<Demand> implements ServerClientHandler, Runnable {

    private Socket socket;
    private SenderXML out;
    private ServerConnection server;

    private boolean active = true;

    public ServerClientHandlerSocket(Socket socket, ServerConnection server) {
        this.socket = socket;
        this.server = server;
    }

    private synchronized boolean isActive(){
        return active;
    }

    private synchronized void send(Message message) {
        try {
            out.reset();
            out.send(message);
            out.flush();
        } catch(IOException e){
            System.err.println(e.getMessage());
        }

    }

    @Override
    public synchronized void closeConnection() {
        send("Connection closed!");
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error when closing socket!");
        }
        active = false;
    }

    private void close() {
        closeConnection();
        System.out.println("Deregistering client...");
        //server.deregisterConnection(this);
        System.out.println("Done!");
    }

    @Override
    public void asyncSend(final Object message){
        new Thread(new Runnable() {
            @Override
            public void run() {
                send(message);
            }
        }).start();
    }

    @Override
    public void run() {
        InputStreamXML in;
        Demand demand;
        try{
            // XML come per il client
            in = new InputStreamXML(socket.getInputStream());
            out = new OutputStreamXML(socket.getOutputStream());
            send("Welcome!\nWhat is your name?");
            Demand read = (Demand) in.readObject();
            demand = read;
            //server.lobby(this, demand);
            while(isActive()){
                read = (Demand) in.readObject();
                notify(read);
            }
        } catch (IOException | NoSuchElementException | ClassNotFoundException e) {
            System.err.println("Error!" + e.getMessage());
        }finally{
            close();
        }
    }
}
