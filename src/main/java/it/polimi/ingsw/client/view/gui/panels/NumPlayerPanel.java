package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;

public class NumPlayerPanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";
    private JButton _2playersButton;
    private JButton _3playersButton;
    private final static int BUTTON_SIZE = 250;

    public NumPlayerPanel() {
        super(imgPath);
        create2PlayerButton();
        create3PlayerButton();
    }

    private void create2PlayerButton() {
        _2playersButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/2_player_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        _2playersButton.setOpaque(false);
        _2playersButton.setContentAreaFilled(false);
        _2playersButton.setBorderPainted(false);
        add(_2playersButton);
    }

    private void create3PlayerButton() {
        _3playersButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/3_player_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        _3playersButton.setOpaque(false);
        _3playersButton.setContentAreaFilled(false);
        _3playersButton.setBorderPainted(false);
        add(_3playersButton);
    }
}