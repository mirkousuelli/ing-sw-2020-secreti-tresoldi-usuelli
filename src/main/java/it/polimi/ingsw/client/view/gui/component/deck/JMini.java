package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;

public class JMini extends JButton {
    private static final int SIZE_X = 65;
    private static final int SIZE_Y = 90;

    private static final String root = "/img/cards/";
    private static final String leaf = "/mini.png";
    private static final String activePath = "/img/labels/active.png";

    private boolean active;
    private boolean enable;

    private final String path;
    private final God god;

    public JMini(God god) {
        super();

        active = false;
        enable = false;
        this.god = god;
        path = root + god.toString().toLowerCase() + leaf;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setName(god.name());

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance( SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        setIcon(icon);
    }

    public String getPath() {
        return this.path;
    }

    public void active() {
        active = true;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(activePath));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        add(new JLabel(icon), new GridBagConstraints());
        validate();
        repaint();
    }

    public void disactive() {
        active = false;

        removeAll();
        validate();
        repaint();
    }

    public boolean isActive() {
        return active;
    }


    public void setEnable() {
        enable = true;
    }

    public void setDisable() {
        enable = false;
    }

    public boolean isEnable() {
        return enable;
    }

    public God getGod() {
        return god;
    }
}
