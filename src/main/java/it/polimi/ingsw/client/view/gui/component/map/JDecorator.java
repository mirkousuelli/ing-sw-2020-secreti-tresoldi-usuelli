package it.polimi.ingsw.client.view.gui.component.map;

import javax.swing.*;

public class JDecorator {
    private JCellStatus decoration;
    private JComponent component;

    public JDecorator(JCellStatus decoration, JComponent component) {
        this.decoration = decoration;
        this.component = component;
    }

    public JDecorator(JCellStatus decoration) {
        this.decoration = decoration;
    }

    public JDecorator() { }

    public JCellStatus getDecoration() {
        return decoration;
    }

    public JComponent getComponent() {
        return component;
    }

    public void setDecoration(JCellStatus decoration) {
        this.decoration = decoration;
    }

    public void setComponent(JComponent component) {
        this.component = component;
    }
}
