package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NumPlayerPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private JButton _2playersButton;
    private JButton _3playersButton;
    private final static int BUTTON_SIZE = 250;

    public NumPlayerPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);
        create2PlayerButton();
        create3PlayerButton();
    }

    private void create2PlayerButton() {
        _2playersButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/2_player_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        _2playersButton.setOpaque(false);
        _2playersButton.setContentAreaFilled(false);
        _2playersButton.setBorderPainted(false);
        _2playersButton.addActionListener(this);
        add(_2playersButton);
    }

    private void create3PlayerButton() {
        _3playersButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/3_player_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        _3playersButton.setOpaque(false);
        _3playersButton.setContentAreaFilled(false);
        _3playersButton.setBorderPainted(false);
        _3playersButton.addActionListener(this);
        add(_3playersButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.panelIndex.next(this.panels);
    }
}