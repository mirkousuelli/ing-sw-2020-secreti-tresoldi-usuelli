package it.polimi.ingsw.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerConnectionSocket implements ServerConnection {
    private final int port;
    private final String FILE = "src/main/java/it/polimi/ingsw/server/network/message/message_lobby-0001.xml"; // X TESTING

    public ServerConnectionSocket(int port){
        this.port = port;
    }

    public void startServer() throws IOException{
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        Socket socket = null;

        try{
            serverSocket = new ServerSocket(port);
        }catch (IOException e){
            System.err.println(e.getMessage()); //port not available
            return;
        }

        System.out.println("Server ready");

        while (true){
            try{
                socket = serverSocket.accept();
                executor.submit(new ServerClientHandlerSocket(socket, this, FILE));
            }
            catch(IOException e){
                break; //In case the serverSocket gets closed
            }
        }

        executor.shutdown();
        if (socket != null) socket.close();
        serverSocket.close();
    }

    //register

    //unregister
}
