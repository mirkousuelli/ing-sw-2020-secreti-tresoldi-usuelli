package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.DemandType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WaitingRoomPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 140;
    private JButton sendButton;
    private JLabel stand;

    public WaitingRoomPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
        createPlayButton();
    }

    public void createWaitStand() {
        GridBagConstraints c = new GridBagConstraints();

        c.gridy = 0;
        c.gridx = 0;
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,200,0,0);

        ImageIcon icon = new ImageIcon("img/labels/waiting_room.png");
        Image img = icon.getImage().getScaledInstance( 420, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, c);
    }

    public void createPlayButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/play_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridy = 0;
        c.gridx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,0,0,200);

        sendButton = new JButton(icon);
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);

        sendButton.addActionListener(this);
        add(sendButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!e.getSource().equals(sendButton)) return;

        this.panelIndex.next(this.panels);
    }

    @Override
    public void updateFromModel(ClientModel clientModel) {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (gui.getAnswer().getContext().equals(DemandType.CHOOSE_DECK))
            mg.setCurrentPanelIndex("chooseCards");
        else if (gui.getAnswer().getContext().equals(DemandType.CHOOSE_CARD))
            mg.setCurrentPanelIndex("chooseGod");
        else {
            gui.free();
            return;
        }

        mg.add(mg.getSantoriniPanelList().get(mg.getCurrentPanelIndex()));
        this.panelIndex.next(this.panels);
    }
}