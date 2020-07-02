package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that represents the panel where the game is paused, either because someone won or because someone closed
 * the application
 * <p>
 * It extends {@link SantoriniPanel}
 */
public class EndPanel extends SantoriniPanel implements ActionListener {

    private static final String EXTENSION = ".png";
    private static final String VICTORY = "victory" + EXTENSION;
    private static final String DEFEAT = "defeat" + EXTENSION;
    private static final String LOST = "lost" + EXTENSION;
    private static final String SAVE = "saved" + EXTENSION;

    private final String type;

    private JButton playAgainButton;
    private JButton quitButton;

    private static final int BUTTON_SIZE = 150;

    /**
     * Constructor of the end panel, which can be for a victory, a defeat, or a save (when a player quits the game, that
     * can later be reloaded)
     *
     * @param type       the type of end panel
     * @param panelIndex the index of the panel
     * @param panels     the panels used
     */
    EndPanel(String type, CardLayout panelIndex, JPanel panels) {
        super(type + EXTENSION, panelIndex, panels);
        this.type = type + EXTENSION;

        createPlayAgainButton();
        createQuitButton();
    }

    /**
     * Function which creates the play again button.
     */
    private void createPlayAgainButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.anchor = GridBagConstraints.SOUTHWEST;
        //c.insets = new Insets(400,0,0,0);

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/play_again.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
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

    /**
     * Function which creates the quit button
     */
    private void createQuitButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 0.5;
        c.weighty = 0.5;
        //c.insets = new Insets(400,0,0,0);
        c.anchor = GridBagConstraints.SOUTHEAST;

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/quit_button.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
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

        switch (src.getName()) {
            case "playAgain":
                gui.generateDemand(DemandType.NEW_GAME, "close");
                if (!type.equals(DEFEAT)) {
                    mg.clean();

                    mg.addPanel(new WaitingRoomPanel(panelIndex, panels));
                    panelIndex.next(panels);
                    if (!type.equals(SAVE))
                        gui.generateDemand(DemandType.NEW_GAME, new ReducedMessage("y"));
                    gui.free();
                }
                break;

            case "quit":
                if (type.equals(VICTORY) || type.equals(LOST))
                    gui.generateDemand(DemandType.NEW_GAME, new ReducedMessage("n"));

                System.exit(1);
                break;

            default:
                break;
        }
    }
}