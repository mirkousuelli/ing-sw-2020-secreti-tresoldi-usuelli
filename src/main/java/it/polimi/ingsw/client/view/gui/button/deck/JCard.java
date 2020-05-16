package it.polimi.ingsw.client.view.gui.button.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;

public class JCard extends JLabel {
    private final static int SIZE_X = 0;
    private final static int SIZE_Y = 0;

    private final static String root = "img/cards/";
    private final static String card = "/card.png";
    private final static String power = "/power.png";
    private final static String malus = "/malus.png";

    private final God god;
    private String path;

    public JCard(God god) {
        this.god = god;
        this.path = root + this.god.toString().toLowerCase() + card;
    }

    public void applyPower() {
        this.path = root + this.god + power;
    }

    public void applyMalus() {
        this.path = root + this.god + malus;
    }

    public void applyNormal() {
        this.path = root + this.god + card;
    }

    public String getPath() {
        return this.path;
    }

    public String getName() {
        return this.god.toString();
    }

    public God getGod() {
        return this.god;
    }
}
