package it.polimi.ingsw.client;

import it.polimi.ingsw.client.network.ClientConnection;
import it.polimi.ingsw.client.network.ClientConnectionSocket;
import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.client.view.cli.SantoriniPrintStream;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

public class Client {

    private static final Scanner in = new Scanner(System.in);
    private static final PrintStream out = new SantoriniPrintStream(System.out);

    public static void main(String[] args) throws IOException {
        ClientView clientView;

        int viewType = Client.askInt("Insert which UI you want: 1-CLI, 2-GUI");

        switch (viewType) {
            case 1:
                clientView = new CLI();
                break;

            case 2:
                clientView = new CLI(); // da mettere GUI, adesso non c'è la struttura
                break;

            default:
                throw new NotAValidInputRunTimeException("Not a valid view");
        }

        new Thread(
                clientView
        ).start();
    }

    private static int askInt(String message) {
        out.println(message);
        String value;

        do {
            value = in.nextLine();
        } while (!value.equals("1") && !value.equals("2"));

        return Integer.parseInt(value);
    }
}
