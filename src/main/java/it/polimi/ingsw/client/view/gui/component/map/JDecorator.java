package it.polimi.ingsw.client.view.gui.component.map;

import javax.swing.*;
import java.awt.*;

public class JDecorator {
    public static final int DIMENSION = 80;
    private JCellStatus decoration;
    private final JLabel component;

    public JDecorator(JCellStatus decoration) {
        this.decoration = decoration;
        this.component = new JLabel();
        this.component.setLayout(new GridBagLayout());

        if (decoration.getPath() != null) {
            ImageIcon icon = new ImageIcon(this.getClass().getResource(decoration.getPath()));
            Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            this.component.setIcon(icon);
        }
        else
            this.component.setIcon(null);

        this.component.revalidate();
    }

    public JDecorator() {
        this.decoration = JCellStatus.NONE;

        this.component = new JLabel();
        this.component.setLayout(new GridBagLayout());
        this.component.setIcon(null);
        this.component.revalidate();
    }

    public JCellStatus getDecoration() {
        return decoration;
    }

    public JLabel getComponent() {
        return component;
    }

    public void setDecoration(JCellStatus decoration) {
        this.decoration = decoration;

        if (decoration.getPath() != null) {
            ImageIcon icon = new ImageIcon(this.getClass().getResource(decoration.getPath()));
            Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            this.component.setIcon(icon);
            this.component.revalidate();
        }
    }

    public void addOver(JLabel component) {
        this.component.add(component, new GridBagConstraints());
        this.component.revalidate();
    }

    public void removeOver() {
        this.component.removeAll();
        this.component.revalidate();
    }
}
