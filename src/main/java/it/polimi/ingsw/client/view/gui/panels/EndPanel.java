package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class EndPanel extends SantoriniPanel {
    public static final String VICTORY = "victory.png";
    public static final String DEFEAT = "defeat.png";
    public static final String LOST = "lost.png";
    private String type;

    public EndPanel(String type) {
        super(type);
        this.type = type;
    }
}