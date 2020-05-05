package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class NumPlayerPanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";
    private JButton _2playersButton;
    private JButton _3playersButton;

    public NumPlayerPanel() {
        super(imgPath);
        create2PlayerButton();
        create3PlayerButton();
    }

    private void create2PlayerButton() {
        _2playersButton = new JButton("2 PLAYERS");
        add(_2playersButton);
    }

    private void create3PlayerButton() {
        _3playersButton = new JButton("3 PLAYERS");
        add(_3playersButton);
    }
}