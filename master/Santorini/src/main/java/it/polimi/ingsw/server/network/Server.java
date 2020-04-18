package it.polimi.ingsw.server.network;

import java.io.IOException;

public class Server {

    public static void main (String [] args ) throws IOException {
        ServerConnectionSocket server = new ServerConnectionSocket(1337);

        try {
            server.startServer();
        }
        catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
