package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPanel extends SantoriniPanel implements ActionListener {
    private final static String imgPath = "intro.png";
    private final static int BUTTON_SIZE = 150;
    private JButton playButton;

    public StartPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createPlayButton();
    }

    private void createPlayButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/play_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,0,-450,0);

        playButton = new JButton(icon);
        playButton.setOpaque(false);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);

        playButton.addActionListener(this);
        add(playButton, c);
    }

    public void actionPerformed(ActionEvent e) {
        ((ManagerPanel) panels).setCurrentPanelIndex("nickName");
        this.panelIndex.next(this.panels);
    }
}