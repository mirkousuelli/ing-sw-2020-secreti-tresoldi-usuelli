package it.polimi.ingsw.server;

import it.polimi.ingsw.server.network.ServerConnectionSocket;

import java.util.logging.Logger;

public class Server {

    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public static void main(String[] args) {
        int port = 1337;

        if (args != null && args.length == 2 && args[0].equals("-p")) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                LOGGER.info("Port malformed! NumberFormatException for input '" + args[1] + "', using default port 1337...");
                port = 1337;
            }
        }

        ServerConnectionSocket server = new ServerConnectionSocket(port);
        server.startServer();
    }
}
