package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ChooseStarterPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 200;
    private JLabel stand;
    private List<JButton> tags;
    private final JGame game;
    private JButton chooseButton;

    public ChooseStarterPanel(CardLayout panelIndex, JPanel panels, JGame game) {
        super(imgPath, panelIndex, panels);

        this.game = game;
        this.tags = new ArrayList<>();

        createWaitStand();
        createChooseButton();
    }

    public void createWaitStand() {
        ImageIcon icon = new ImageIcon("img/labels/lobby.png");
        Image img = icon.getImage().getScaledInstance(420, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, new GridBagConstraints());
    }

    void addPlayers(List<JPlayer> players) {
        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < players.size(); i++) {
            c.gridx = 0;
            c.gridy = i;

            players.get(i).addActionListener(this);

            this.tags.add(players.get(i));
            this.stand.add(this.tags.get(i), c);
        }
    }

    public void createChooseButton() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = this.game.getNumPlayer();

        ImageIcon icon = new ImageIcon("img/buttons/choose_starter.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        chooseButton = new JButton(icon);
        chooseButton.setPreferredSize(new Dimension(BUTTON_SIZE,200));
        chooseButton.setOpaque(false);
        chooseButton.setContentAreaFilled(false);
        chooseButton.setBorderPainted(false);
        chooseButton.addActionListener(this);
        chooseButton.setName("choose");

        stand.add(chooseButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (((JButton)e.getSource()).getName()) {
            case "player":
                this.game.setCurrentPlayer((JPlayer) e.getSource());
                break;
            case "choose":
                ManagerPanel mg = (ManagerPanel) panels;

                mg.addPanel(new GamePanel(panelIndex, panels));
                this.panelIndex.next(this.panels);
                mg.getGui().generateDemand(DemandType.CHOOSE_STARTER, new ReducedMessage(((JButton)e.getSource()).getText()));
                break;
        }
    }
}
