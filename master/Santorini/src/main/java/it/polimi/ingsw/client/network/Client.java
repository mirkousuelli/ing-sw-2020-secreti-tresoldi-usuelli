package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.view.CLI;
import it.polimi.ingsw.client.view.ClientView;

import java.io.IOException;

public class Client {
    public static void main(String[] args) throws IOException {
        ClientConnectionSocket client = new ClientConnectionSocket("127.0.0.1", 1337);

        try{
            client.startClient();
        }
        catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
