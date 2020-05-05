package it.polimi.ingsw;

import it.polimi.ingsw.client.view.gui.GUI;

import javax.swing.*;

public class Santorini {
    public static void main( String[] args ) {
        SwingUtilities.invokeLater(() -> {
            GUI app = new GUI();
            app.createAndStartGUI();
        });
    }
}
