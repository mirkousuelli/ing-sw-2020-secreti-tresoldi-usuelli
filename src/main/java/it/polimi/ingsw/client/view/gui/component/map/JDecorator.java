package it.polimi.ingsw.client.view.gui.component.map;

import javax.swing.*;
import java.awt.*;

public class JDecorator {
    public static final int DIMENSION = 80;
    private JCellStatus decoration;
    private final JLabel component;

    public JDecorator(JCellStatus decoration) {
        this.decoration = decoration;
        component = new JLabel();
        component.setLayout(new GridBagLayout());

        if (decoration.getPath() != null) {
            ImageIcon icon = new ImageIcon(this.getClass().getResource(decoration.getPath()));
            Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            component.setIcon(icon);
        } else
            component.setIcon(null);

        component.revalidate();
    }

    public JDecorator() {
        decoration = JCellStatus.NONE;

        component = new JLabel();
        component.setLayout(new GridBagLayout());
        component.setIcon(null);
        component.revalidate();
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
            component.setIcon(icon);
            component.revalidate();
        }
    }

    public void addOver(JLabel component) {
        this.component.add(component, new GridBagConstraints());
        this.component.revalidate();
    }

    public void removeOver() {
        component.removeAll();
        component.revalidate();
    }
}
