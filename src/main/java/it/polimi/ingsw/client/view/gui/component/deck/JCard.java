package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;

/**
 * Class that represents the Card in the GUI.
 * <p>
 * It contains its size and the God
 */
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

    /**
     * Constructor of the card, given the God
     *
     * @param god the God that the card is based on
     */
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

    /**
     * Constructor of the card
     */
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

    /**
     * Method that displays the card when the power is active (the card will have yellow lights on his board)
     */
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

    /**
     * Method that returns the card to the normal visual (for example after the power is turned off)
     */
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
