package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    private CardLayout cardLayout;

    public ManagerPanel() {
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        add(new StartPanel(cardLayout, this));
        add(new NicknamePanel(cardLayout, this));
        add(new NumPlayerPanel(cardLayout, this));
        add(new WaitingRoomPanel(cardLayout, this));
        add(new ChooseCardsPanel(cardLayout, this));
        add(new ChooseGodPanel(cardLayout, this));
        add(new GamePanel(cardLayout, this));
        add(new EndPanel(EndPanel.DEFEAT, cardLayout, this));

        cardLayout.show(this, "Card 1");
    }
}
