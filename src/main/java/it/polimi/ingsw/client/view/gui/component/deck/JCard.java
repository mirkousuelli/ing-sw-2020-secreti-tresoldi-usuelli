package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;

public class JCard extends JLabel {
    private final static int SIZE_X = 170; //190;
    private final static int SIZE_Y = 280 ; //= 310;

    private final static String root = "/img/cards/";
    private final static String card = "/card.png";
    private final static String power = "/power.png";
    private final static String malus = "/malus.png";
    private final static String retro = "retro.png";

    private God god;
    private String path;

    public JCard(God god) {
        this.god = god;
        this.path = root + this.god.toString().toLowerCase() + card;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);

        setOpaque(false);
        setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
        setLayout(new GridBagLayout());
        setName("card");
    }

    public JCard() {
        this.path = root + retro;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);

        setOpaque(false);
        setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
        setLayout(new GridBagLayout());
        setName("card");

        repaint();
        validate();
    }

    public void applyPower() {
        this.path = root + this.god + power;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
        setName("power");

        repaint();
        validate();
    }

    public void applyMalus() {
        this.path = root + this.god + malus;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
        setName("malus");

        repaint();
        validate();
    }

    public void applyNormal() {
        this.path = root + this.god + card;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
        setName("card");

        repaint();
        validate();
    }

    public String getPath() {
        return this.path;
    }

    public String getGodName() {
        return this.god.toString();
    }

    public God getGod() {
        return this.god;
    }
}
