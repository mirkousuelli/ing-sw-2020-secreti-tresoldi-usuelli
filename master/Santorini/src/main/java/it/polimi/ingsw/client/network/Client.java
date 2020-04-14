package it.polimi.ingsw.client.network;

import java.io.IOException;

public class Client {
    public static void main(String[] args){
        ClientConnectionSocket client = new ClientConnectionSocket("127.0.0.1", 1337);
        try{
            client.startClient();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}
