package it.polimi.ingsw.client.network;

import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.FileXML;
import it.polimi.ingsw.communication.observer.Observable;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientConnectionSocket<S> extends Observable<Answer<S>> implements ClientConnection<S> {
    //private String ip;    SECONDO ME SONO INUTILI PERCHE SERVONO SOLO AD APRIRE LA SOCKET
    //private int port;     SECONDO ME SONO INUTILI PERCHE SERVONO SOLO AD APRIRE LA SOCKET
    Socket socket;
    FileXML file;
    private final String FILE = "src/main/java/it/polimi/ingsw/client/network/message/message.xml"; // X TESTING

    public ClientConnectionSocket(String ip, int port) throws IOException {
        //this.ip = ip;     SECONDO ME SONO INUTILI PERCHE SERVONO SOLO AD APRIRE LA SOCKET
        //this.port = port; SECONDO ME SONO INUTILI PERCHE SERVONO SOLO AD APRIRE LA SOCKET
        socket = new Socket(ip, port);
        //this.file = file;
    }

    public void startClient() throws IOException {
        boolean testDemand = true; // DA CAMBIARE anche in ServerClientHandlerSocket !!!!!!!!!!!!!
        file = new FileXML(FILE, socket);

        System.out.println("Connection established");

        try{
            //while (true){        L'HO TOLTO PER TESTING
            if (testDemand) {
                System.out.println("Sending...");
                file.send(new Demand(DemandType.JOIN_GAME, "1234"));
                System.out.println("Sent!");
            } else {
                System.out.println("Receiving...");
                Answer answer = (Answer) file.receive();
                System.out.println("Received!");
            }
            //}
        } catch(NoSuchElementException e){
            System.out.println("Connection closed");
        }

        socket.close();
    }

    @Override
    public void update(Demand<S> demand) {
        try {
            file.send(demand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() throws IOException {
        socket.close();
    }
}