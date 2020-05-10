package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class ChooseGodPanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";

    public ChooseGodPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);
    }
}