package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.communication.message.header.DemandType;

import javax.swing.*;
import java.awt.*;

public class WaitingRoomPanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";
    private JLabel stand;

    public WaitingRoomPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
    }

    public void createWaitStand() {
        ImageIcon icon = new ImageIcon("img/buttons/waiting.png");
        Image img = icon.getImage().getScaledInstance( 800, 400, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        stand = new JLabel(icon);
        stand.setOpaque(false);

        add(stand, new GridBagConstraints());
    }

    @Override
    public void updateFromModel() {
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (gui.getClientModel().getCurrentState().equals(DemandType.CHOOSE_DECK)) {
            mg.setCurrentPanelIndex("chooseCards");
            ((ChooseCardsPanel) mg.getCurrentPanel()).numPlayer = gui.getClientModel().getNumberOfPlayers();
            gui.free();
        }
        else if (gui.getClientModel().getCurrentState().equals(DemandType.CHOOSE_CARD)) {
            mg.setCurrentPanelIndex("chooseGod");
            (mg.getSantoriniPanelList().get(mg.getCurrentPanelIndex())).updateFromModel();
        }
        else {
            gui.free();
            return;
        }

        mg.add(mg.getSantoriniPanelList().get(mg.getCurrentPanelIndex()));
        this.panelIndex.next(this.panels);
    }
}