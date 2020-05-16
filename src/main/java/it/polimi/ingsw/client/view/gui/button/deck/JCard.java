package it.polimi.ingsw.client.view.gui.button.deck;

import javax.swing.*;

public class JCard extends JLabel {
    private final String path;

    public JCard(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
