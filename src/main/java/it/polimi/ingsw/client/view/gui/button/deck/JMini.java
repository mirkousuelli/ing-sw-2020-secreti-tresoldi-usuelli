package it.polimi.ingsw.client.view.gui.button.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;

public class JMini extends JButton {
    private final static int SIZE_X = 0;
    private final static int SIZE_Y = 0;

    private final static String root = "img/cards/";
    private final static String leaf = "/mini.png";

    private final String path;

    public JMini(God god) {
        this.path = root + god.toString().toLowerCase() + leaf;
    }

    public String getPath() {
        return this.path;
    }
}
