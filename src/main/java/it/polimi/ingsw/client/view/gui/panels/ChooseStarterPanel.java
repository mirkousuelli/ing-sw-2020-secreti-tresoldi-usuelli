package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the panel where the Challenger has to decide the player that will start playing first.
 * <p>
 * It extends {@link SantoriniPanel}
 */
public class ChooseStarterPanel extends SantoriniPanel implements ActionListener {

    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 200;

    private JLabel stand;
    private final List<JButton> tags;
    private final JGame game;
    private JButton chooseButton;

    /**
     * Constructor of the panel which contains the nicknames of all players in the game, among which the Challenger
     * has to pick the one of the starter.
     *
     * @param panelIndex the index of the panel
     * @param panels     the panels used
     * @param game       the game that is being played
     */
    public ChooseStarterPanel(CardLayout panelIndex, JPanel panels, JGame game) {
        super(imgPath, panelIndex, panels);

        this.game = game;
        tags = new ArrayList<>();

        createWaitStand();
        createChooseButton();
    }

    /**
     * Method which create the main label in the middle of the screen where will be displayed players' name to be
     * chosen.
     */
    private void createWaitStand() {
        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/labels/lobby.png"));
        Image img = icon.getImage().getScaledInstance(420, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, new GridBagConstraints());
    }

    /**
     * Method which allows to add a list of players to the stand.
     *
     * @param players list of players
     */
    void addPlayers(List<JPlayer> players) {
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < players.size(); i++) {
            c.gridx = 0;
            c.gridy = i;

            players.get(i).addActionListener(this);

            tags.add(players.get(i));
            stand.add(tags.get(i), c);
        }
    }

    /**
     * Create the button that allows to choose the starter selected.
     */
    private void createChooseButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = game.getNumPlayer();

        ImageIcon icon = new ImageIcon(this.getClass().getResource("/img/buttons/choose_starter.png"));
        Image img = icon.getImage().getScaledInstance(BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        chooseButton = new JButton(icon);
        chooseButton.setPreferredSize(new Dimension(BUTTON_SIZE, 200));
        chooseButton.setOpaque(false);
        chooseButton.setContentAreaFilled(false);
        chooseButton.setBorderPainted(false);
        chooseButton.addActionListener(this);
        chooseButton.setName("choose");

        stand.add(chooseButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((JButton) e.getSource()).getName()) {
            case "player":
                game.setCurrentPlayer((JPlayer) e.getSource());
                break;
            case "choose":
                for (JPlayer p : game.getPlayerList()) {
                    p.removeActionListener(this);
                }

                ManagerPanel mg = (ManagerPanel) panels;

                mg.addPanel(new GamePanel(panelIndex, panels));
                panelIndex.next(panels);
                mg.getGui().generateDemand(DemandType.CHOOSE_STARTER, new ReducedMessage(game.getCurrentPlayer().getNickname()));
                break;
        }
    }
}
