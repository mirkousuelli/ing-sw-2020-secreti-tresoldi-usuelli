package it.polimi.ingsw.server;


import it.polimi.ingsw.server.network.ServerConnectionSocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main (String[] args) {
        ServerConnectionSocket server = new ServerConnectionSocket(1337);

        try {
            server.startServer();
        }
        catch(IOException e) {
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
        }
    }
}
