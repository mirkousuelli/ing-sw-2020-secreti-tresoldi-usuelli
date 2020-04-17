package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.communication.message.Answer;
import it.polimi.ingsw.communication.message.Demand;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.Message;
import it.polimi.ingsw.communication.message.xml.network.InputStreamXML;
import it.polimi.ingsw.communication.message.xml.network.OutputStreamXML;
import it.polimi.ingsw.communication.observer.Observable;

import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;

public class ClientConnectionSocket<S> extends Observable<Answer<S>> implements ClientConnection<S> {
    private String ip;
    private int port;

    public ClientConnectionSocket(String ip, int port, ClientView<S> clientView){
        this.ip = ip;
        this.port = port;
        this.addObserver(clientView);
    }

    public void startClient() throws IOException {

        Socket socket = new Socket(ip, port);

        System.out.println("Connection established");

        //Scanner socketIn = new Scanner(socket.getInputStream());
        //PrintWriter socketOut = new PrintWriter(socket.getOutputStream());
        //Scanner stdin = new Scanner(System.in);
        OutputStreamXML socketOut = new OutputStreamXML();
        InputStreamXML socketIn = new InputStreamXML();

        try{
            while (true){
                //String inputLine = stdin.nextLine();
                Message msg = new Demand(DemandType.JOIN_GAME, "1234");
                //socketOut.println(/*inputLine*/);
                //socketOut.flush();
                socketOut.send(msg);
                String socketLine = socketIn.nextLine();
                System.out.println(socketLine);
            }
        } catch(NoSuchElementException e){
            System.out.println("Connection closed");
        } finally {
            //stdin.close();
            socketIn.close();
            socketOut.close();
            socket.close();
        }
    }

    @Override
    public void update(Demand<S> message) {

    }

    @Override
    public void closeConnection() {
        socketIn.close();
        socketOut.close();
        socket.close();
    }


}