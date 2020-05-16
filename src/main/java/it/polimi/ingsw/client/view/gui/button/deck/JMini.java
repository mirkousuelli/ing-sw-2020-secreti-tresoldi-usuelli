package it.polimi.ingsw.client.view.gui.button.deck;

import javax.swing.*;

public class JMini extends JButton {
    private final String path;

    public JMini(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }
}
