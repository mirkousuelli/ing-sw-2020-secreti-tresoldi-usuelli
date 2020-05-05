package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class ChooseCardsPanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";
    JButton sendButton;
    JButton removeButton;

    public ChooseCardsPanel() {
        super(imgPath);
        createSendButton();
        createRemoveButton();
    }

    private void createSendButton() {
        sendButton = new JButton("SEND");
        add(sendButton);
    }

    private void createRemoveButton() {
        removeButton = new JButton("REMOVE");
        add(removeButton);
    }
}