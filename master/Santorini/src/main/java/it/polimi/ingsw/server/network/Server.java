package it.polimi.ingsw.server.network;

import java.io.IOException;

public class Server {
    public Server()
    {
        MultiTaskServer server = new MultiTaskServer(1337);
        try {
            server.startServer();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
