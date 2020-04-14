package it.polimi.ingsw.server.network;

import java.io.IOException;

public class Server {

    public Server()
    {
        ServerManager server = new ServerManager(1337);

        try {
            server.startServer();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
