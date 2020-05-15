package it.polimi.ingsw.client.view.gui.button;

import javax.swing.*;

public class JBlock extends JCell {
    public static final String NONE = null;
    public static final String BOTTOM = "img/blocks/bottom.png";
    public static final String MIDDLE = "img/blocks/middle.png";
    public static final String TOP = "img/blocks/top.png";

    public JBlock() {
        super();
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
        this.setIcon(new ImageIcon(status));
    }
}
