package it.polimi.ingsw.client.view.gui.button;

import javax.swing.*;

public abstract class JCell extends JButton {
    private static final String NONE = null;
    protected String status;

    JCell() {
        super();
        this.status = JCell.NONE;
        this.setRolloverEnabled(true);
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
