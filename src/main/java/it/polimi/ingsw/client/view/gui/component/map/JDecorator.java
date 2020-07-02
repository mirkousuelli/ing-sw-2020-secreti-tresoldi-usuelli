package it.polimi.ingsw.client.view.gui.component.map;

import javax.swing.*;
import java.awt.*;

/**
 * Class that represents the decorator in the GUI.
 * <p>
 * It contains its dimension, decoration and component
 */
public class JDecorator {
    public static final int DIMENSION = 80;
    private JCellStatus decoration;
    private final JLabel component;

    /**
     * Constructor of the JDecorator given the status of the cell
     *
     * @param decoration the decoration of the decorator
     */
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

    /**
     * Method that adds the given component over the decorator
     *
     * @param component the component that is added
     */
    public void addOver(JLabel component) {
        this.component.add(component, new GridBagConstraints());
        this.component.revalidate();
    }

    /**
     * Method that removes any component from the decorator
     */
    public void removeOver() {
        component.removeAll();
        component.revalidate();
    }
}
