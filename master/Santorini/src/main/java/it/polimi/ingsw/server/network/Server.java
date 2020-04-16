package it.polimi.ingsw.server.network;

import java.io.IOException;

public class Server {
    private ServerConnection server;

    public Server()
    {
        server = new ServerConnectionSocket(1337);

        try {
            ((ServerConnectionSocket) server).startServer();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
