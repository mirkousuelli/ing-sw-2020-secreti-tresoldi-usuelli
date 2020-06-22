package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.SantoriniRunnable;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

import javax.swing.*;

public class Client {

    public static void main(String[] args) {
        final SantoriniRunnable[] clientView = new ClientView[1];

        //if (System.console() != null)
            clientView[0] = new CLI<>();
        /*else
            SwingUtilities.invokeLater(() -> {
                clientView[0] = new GUI<>();
                ((GUI) clientView[0]).createAndStartGUI();
            });*/

        new Thread(
                clientView[0]
        ).start();
    }
}
