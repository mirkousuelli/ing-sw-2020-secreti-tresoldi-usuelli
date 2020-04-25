package it.polimi.ingsw.client;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.GUI;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    private static final Logger LOGGER = Logger.getLogger(Client.class.getName());
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        ClientView clientView;
        ClientConnection clientConnection;
        ClientModel clientModel;

        String name = Client.askString("Insert your name:");

        int connectionType = Client.askInt("Insert which type of connection you want: 1-Socket");
        String ip = Client.askString("Insert the server's ip:");
        int port = Client.askInt("Insert the server's port:");

        int viewType = Client.askInt("Insert which UI you want: 1-CLI, 2-GUI");

        switch (connectionType) {
            case 1:
                clientConnection = new ClientConnectionSocket(ip, port);
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid connection type");
        }

        switch (viewType) {
            case 1:
                clientView = new CLI(name, clientConnection);
                break;

            case 2:
                clientView = new GUI(name, clientConnection);
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid view");
        }

        clientModel = new ClientModel(name);

        //set up observers
        clientConnection.addObserver(clientModel);
        clientModel.addObserver(clientView);
        clientView.addObserver(clientConnection);

        try{
            clientConnection.startClient();
        }
        catch (IOException e){
            LOGGER.log(Level.SEVERE, "Got an IOException", e);
        }

        clientView.run(clientModel);
    }

    private static String askString(String message) {
        System.out.println(message);

        return in.nextLine();
    }

    private static int askInt(String message) {
        return Integer.parseInt(Client.askString(message));
    }
}
