package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndPanel extends SantoriniPanel implements ActionListener {
    public static final String VICTORY = "victory.png";
    public static final String DEFEAT = "defeat.png";
    public static final String LOST = "lost.png";
    public static final String SAVE = "saved.png";
    private String type;
    private JButton playAgainButton;
    private JButton quitButton;
    private final static int BUTTON_SIZE = 150;

    public EndPanel(String type, CardLayout panelIndex, JPanel panels) {
        super(type, panelIndex, panels);
        this.type = type;

        createPlayAgainButton();
        createQuitButton();
    }

    void createPlayAgainButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.SOUTHWEST;
        //c.insets = new Insets(400,0,0,0);

        ImageIcon icon = new ImageIcon("img/buttons/play_again.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        playAgainButton = new JButton(icon);
        playAgainButton.setName("playAgain");
        playAgainButton.addActionListener(this);
        playAgainButton.setOpaque(false);
        playAgainButton.setContentAreaFilled(false);
        playAgainButton.setBorderPainted(false);

        add(playAgainButton, c);

        validate();
        repaint();
    }

    void createQuitButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        //c.insets = new Insets(400,0,0,0);
        c.anchor = GridBagConstraints.SOUTHEAST;

        ImageIcon icon = new ImageIcon("img/buttons/quit_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        quitButton = new JButton(icon);
        quitButton.setName("quit");
        quitButton.addActionListener(this);
        quitButton.setOpaque(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorderPainted(false);

        add(quitButton, c);

        validate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton src = ((JButton) e.getSource());

        switch(src.getName()) {
            case "playAgain":
                break;

            case "quit":
                break;

            default:
                break;
        }
    }
}