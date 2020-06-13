package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;

public class JMini extends JButton {
    private final static int SIZE_X = 65;
    private final static int SIZE_Y = 90;

    private final static String root = "/img/cards/";
    private final static String leaf = "/mini.png";
    private final static String activePath = "/img/labels/active.png";

    private boolean active;
    private boolean enable;
    private final String path;
    private final God god;

    public JMini(God god) {
        super();

        this.active = false;
        this.enable = false;
        this.god = god;
        this.path = root + god.toString().toLowerCase() + leaf;

        setLayout(new GridBagLayout());
        setPreferredSize(new Dimension(SIZE_X, SIZE_Y));
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setName("mini");

        ImageIcon icon = new ImageIcon(this.getClass().getResource(path));
        Image img = icon.getImage().getScaledInstance( SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        setIcon(icon);
    }

    public String getPath() {
        return this.path;
    }

    public void active() {
        this.active = true;

        ImageIcon icon = new ImageIcon(this.getClass().getResource(activePath));
        Image img = icon.getImage().getScaledInstance(SIZE_X, SIZE_Y, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        add(new JLabel(icon), new GridBagConstraints());
        validate();
        repaint();
    }

    public void disactive() {
        this.active = false;

        removeAll();
        validate();
        repaint();
    }

    public boolean isActive() {
        return this.active;
    }


    public void setEnable() {
        this.enable = true;
    }

    public void setDisable() {
        this.enable = false;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public God getGod() {
        return this.god;
    }
}
