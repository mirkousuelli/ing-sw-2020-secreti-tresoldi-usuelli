package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.cli.NotAValidInputRunTimeException;
import it.polimi.ingsw.client.view.cli.SantoriniPrintStream;
import it.polimi.ingsw.client.view.gui.GUI;

import java.io.PrintStream;
import java.util.Scanner;

public class Client {

    private static final Scanner in = new Scanner(System.in);
    private static final PrintStream out = new SantoriniPrintStream(System.out);

    public static void main(String[] args) {
        ClientView clientView;

        int viewType = Client.askInt("Insert which UI you want: 1-CLI, 2-GUI");

        switch (viewType) {
            case 1:
                clientView = new CLI();
                break;

            case 2:
                clientView = new GUI();
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
