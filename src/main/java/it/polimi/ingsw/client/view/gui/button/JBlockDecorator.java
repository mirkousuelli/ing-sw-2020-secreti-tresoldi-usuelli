package it.polimi.ingsw.client.view.gui.button;

import javax.swing.*;

public class JBlockDecorator extends JCell {
    public static final String DOME = "img/blocks/dome.png";
    public static final String MOVE = "img/blocks/move.png";
    public static final String BUILD = "img/blocks/build.png";
    public static final String MALUS = "img/blocks/malus.png";
    public static final String USE_POWER = "img/blocks/use_power.png";

    private JCell block;

    JBlockDecorator(JCell block) {
        super();
        this.block = block;
    }

    @Override
    public void setStatus(String status) {
        super.setStatus(status);
        this.setRolloverIcon(new ImageIcon(status));
    }
}
