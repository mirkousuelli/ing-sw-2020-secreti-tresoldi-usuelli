package it.polimi.ingsw.client;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.client.view.cli.SantoriniPrintStream;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Client {

    private static final Scanner in = new Scanner(System.in);
    private static final PrintStream out = new SantoriniPrintStream(System.out);

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

        clientModel = new ClientModel(name, clientConnection);

        switch (viewType) {
            case 1:
                clientView = new CLI(clientModel);
                break;

            case 2:
                clientView = new CLI(clientModel); // da mettere GUI, adesso non c'è la struttura
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid view");
        }

        clientConnection.setClientView(clientView);

        new Thread(
                clientView
        ).start();

        new Thread(
                clientConnection::run
        ).start();

        new Thread(
                clientModel
        ).start();
    }

    private static String askString(String message) {
        out.println(message);

        return in.nextLine();
    }

    private static int askInt(String message) {
        return Integer.parseInt(Client.askString(message));
    }
}
