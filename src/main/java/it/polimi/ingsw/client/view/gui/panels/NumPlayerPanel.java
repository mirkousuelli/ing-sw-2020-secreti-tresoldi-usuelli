package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that represents the panel where the Challenger has to decide how many players will be in his game
 * <p>
 * It extends {@link SantoriniPanel}
 */
public class NumPlayerPanel extends SantoriniPanel implements ActionListener {

    private static final String imgPath = "menu.png";

    private JButton _2playersButton;
    private JButton _3playersButton;

    private static final int BUTTON_SIZE = 250;

    /**
     * Constructor of the panel where the challenger has to click on the button corresponding to the number of players
     * he wants to play with
     *
     * @param panelIndex the index of the panel
     * @param panels     the panels used
     */
    public NumPlayerPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);
        create2PlayerButton();
        create3PlayerButton();
    }

    /**
     * Function which display the 2 players choice button in the middle of the screen.
     */
    private void create2PlayerButton() {
        _2playersButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon(this.getClass().getResource("/img/buttons/2_player_button.png")), BUTTON_SIZE, BUTTON_SIZE));
        _2playersButton.setOpaque(false);
        _2playersButton.setContentAreaFilled(false);
        _2playersButton.setBorderPainted(false);
        _2playersButton.addActionListener(this);
        add(_2playersButton);
    }

    /**
     * Function which display the 3 players choice button in the middle of the screen.
     */
    private void create3PlayerButton() {
        _3playersButton = new JButton(BackgroundPanel.getScaledImage(
                new ImageIcon(this.getClass().getResource("/img/buttons/3_player_button.png")), BUTTON_SIZE, BUTTON_SIZE));
        _3playersButton.setOpaque(false);
        _3playersButton.setContentAreaFilled(false);
        _3playersButton.setBorderPainted(false);
        _3playersButton.addActionListener(this);
        add(_3playersButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String numberOfPlayers = null;
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (e.getSource().equals(_2playersButton))
            numberOfPlayers = "2";
        else if (e.getSource().equals(_3playersButton))
            numberOfPlayers = "3";

        if (numberOfPlayers != null) {
            mg.addPanel(new WaitingRoomPanel(panelIndex, panels));
            this.panelIndex.next(this.panels);

            gui.generateDemand(DemandType.CREATE_GAME, new ReducedMessage(numberOfPlayers));
        }
    }
}