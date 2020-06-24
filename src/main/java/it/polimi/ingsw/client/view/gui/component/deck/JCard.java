package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;

public class JCard extends JLabel {

    private static final int SIZE_X = 170; //190;
    private static final int SIZE_Y = 280; //= 310;

    private static final String root = "/img/cards/";
    private static final String card = "/card.png";
    private static final String power = "/power.png";
    private static final String malus = "/malus.png";
    private static final String retro = "retro.png";

    private God god;
    private String path;

    public JCard(God god) {
        this.god = god;
        path = root + this.god.toString().toLowerCase() + card;

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
        path = root + retro;

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
        path = root + god.toString().toLowerCase() + power;

        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
        setName("power");

        repaint();
        validate();
    }

    public void applyMalus() {
        path = root + god.toString().toLowerCase() + malus;

        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
        setName("malus");

        repaint();
        validate();
    }

    public void applyNormal() {
        path = root + god.toString().toLowerCase() + card;

        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        setIcon(icon);
        setName("card");

        repaint();
        validate();
    }

    public String getPath() {
        return path;
    }

    public String getGodName() {
        return god.toString();
    }

    public God getGod() {
        return god;
    }
}
