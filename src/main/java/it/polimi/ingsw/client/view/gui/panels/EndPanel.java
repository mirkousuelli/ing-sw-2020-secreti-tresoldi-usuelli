package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EndPanel extends SantoriniPanel implements ActionListener {
    private static final String EXTENSION =  ".png";
    public static final String VICTORY = "victory" + EXTENSION;
    public static final String DEFEAT = "defeat" + EXTENSION;
    public static final String LOST = "lost" + EXTENSION;
    public static final String SAVE = "saved" + EXTENSION;

    private final String type;

    private JButton playAgainButton;
    private JButton quitButton;

    private static final int BUTTON_SIZE = 150;

    public EndPanel(String type, CardLayout panelIndex, JPanel panels) {
        super(type + EXTENSION, panelIndex, panels);
        this.type = type + EXTENSION;

        createPlayAgainButton();
        createQuitButton();
    }

    private void createPlayAgainButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.SOUTHWEST;
        //c.insets = new Insets(400,0,0,0);

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/play_again.png"));
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        playAgainButton = new JButton(icon);
        playAgainButton.setName("playAgain");
        playAgainButton.addActionListener(this);
        playAgainButton.setOpaque(false);
        playAgainButton.setContentAreaFilled(false);
        playAgainButton.setBorderPainted(false);

        add(playAgainButton, c);

        if (type.equals(VICTORY))
            playAgainButton.setVisible(true);

        validate();
        repaint();
    }

    private void createQuitButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        //c.insets = new Insets(400,0,0,0);
        c.anchor = GridBagConstraints.SOUTHEAST;

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/quit_button.png"));
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

    void disablePLayAgainButton() {
        playAgainButton.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();
        JButton src = ((JButton) e.getSource());

        switch(src.getName()) {
            case "playAgain":
                if (!type.equals(DEFEAT)) {
                    gui.generateDemand(DemandType.NEW_GAME, new ReducedMessage("y"));

                    mg.clean();

                    mg.addPanel(new WaitingRoomPanel(panelIndex, panels));
                    mg.getCurrentPanel().updateFromModel();
                    this.panelIndex.next(this.panels);
                    gui.free();
                }
                break;

            case "quit":
                if (!type.equals(DEFEAT))
                    gui.generateDemand(DemandType.NEW_GAME, new ReducedMessage("n"));

                System.exit(1);
                break;

            default:
                break;
        }
    }
}