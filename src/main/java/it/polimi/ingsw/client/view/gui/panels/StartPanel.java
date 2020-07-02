package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Class that represents the panel that is shown first when the game is launched
 * <p>
 * It extends {@link SantoriniPanel}
 */
public class StartPanel extends SantoriniPanel implements ActionListener {

    private static final String imgPath = "intro.png";
    private static final int BUTTON_SIZE = 150;
    private JButton playButton;

    /**
     * Constructor of the start panel, which creates also the play button
     *
     * @param panelIndex index of the panel
     * @param panels     the panels used
     */
    public StartPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createPlayButton();
    }

    /**
     * Function which generate the play button and establish its format inside the panel
     */
    private void createPlayButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/play_button.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0, 0, -450, 0);

        playButton = new JButton(icon);
        playButton.setOpaque(false);
        playButton.setContentAreaFilled(false);
        playButton.setBorderPainted(false);

        playButton.addActionListener(this);
        add(playButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(playButton)) return;

        panelIndex.next(panels);
    }
}