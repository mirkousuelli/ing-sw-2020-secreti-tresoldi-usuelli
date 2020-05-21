package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;

public class JCard extends JLabel {
    private final static int SIZE_X = 190;//= 170;
    private final static int SIZE_Y = 310 ;//= 280;

    private final static String root = "img/cards/";
    private final static String card = "/card.png";
    private final static String power = "/power.png";
    private final static String malus = "/malus.png";
    private final static String retro = "retro.png";

    private God god;
    private String path;

    public JCard(God god) {
        this.god = god;
        this.path = root + this.god.toString().toLowerCase() + card;

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
    }

    public JCard() {
        this.path = root + retro;

        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
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
