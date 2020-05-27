package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;

import javax.swing.*;
import java.awt.*;

public class ManagerPanel extends JPanel {
    private final CardLayout cardLayout;
    private SantoriniPanel currentPanel;
    private final GUI gui;
    private JGame game;
    private JPlayer clientPlayer;

    public ManagerPanel(GUI gui) {
        this.gui = gui;
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        game = new JGame();

        addPanel(new EndPanel(EndPanel.SAVE, cardLayout, this));
        addPanel(new StartPanel(cardLayout, this));
        addPanel(new NicknamePanel(cardLayout, this));
        cardLayout.show(this, "Card 1");
    }

    public GUI getGui() {
        return gui;
    }

    public JGame getGame() {
        return game;
    }

    public JPlayer getClientPlayer() {
        return clientPlayer;
    }

    public Component addPanel(Component comp) {
        currentPanel = (SantoriniPanel) comp;
        return this.add(comp);
    }

    public SantoriniPanel getCurrentPanel() {
        return currentPanel;
    }
}
