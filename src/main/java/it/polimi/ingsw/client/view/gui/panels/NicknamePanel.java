package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class NicknamePanel extends SantoriniPanel {
    private static final String imgPath = "menu.png";
    private final String SUBMIT = "SUBMIT";
    private final String NICKNAME = "Nickname";
    private JButton submitButton;
    private JTextField nicknameField;

    public NicknamePanel() {
        super(imgPath);
        createNicknameField();
        createSubmitButton();
    }

    private void createSubmitButton() {
        submitButton = new JButton(SUBMIT);
        add(submitButton);
    }

    private void createNicknameField() {
        nicknameField = new JTextField(10);
        add(nicknameField);
    }
}