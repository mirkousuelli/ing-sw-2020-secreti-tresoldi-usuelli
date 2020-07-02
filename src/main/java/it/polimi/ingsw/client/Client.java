package it.polimi.ingsw.client;

import it.polimi.ingsw.client.view.ClientView;
import it.polimi.ingsw.client.view.SantoriniRunnable;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.gui.GUI;

import javax.swing.*;

/**
 * Class which allows to run a client-side instance of the project
 */
public class Client {

    /**
     * Main method that runs the GUI or the CLI: if the person runs the jar via command line then the game will
     * start in CLI, if he opens it through double click on the file it will start in GUI
     *
     * @param args
     */
    public static void main(String[] args) {
        final SantoriniRunnable[] clientView = new ClientView[1];

        if (System.console() != null)
            clientView[0] = new CLI<>();
        else
            SwingUtilities.invokeLater(() -> {
                clientView[0] = new GUI<>();
                ((GUI) clientView[0]).createAndStartGUI();
            });

        new Thread(
                clientView[0]
        ).start();
    }
}
