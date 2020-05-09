package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class StartPanel extends SantoriniPanel {
    private final static String imgPath = "intro.png";
    private final static int BUTTON_SIZE = 200;
    private JButton playButton;

    public StartPanel() {
        super(imgPath);

        createPlayButton();
    }

    private void createPlayButton() {
        GridBagConstraints c = new GridBagConstraints();
        JPanel button = new JPanel(new BorderLayout());

        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,0,-450,0);

        playButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon("img/buttons/play_button.png"), BUTTON_SIZE, BUTTON_SIZE));
        playButton.setOpaque(false);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);

        add(playButton, c);
    }
}