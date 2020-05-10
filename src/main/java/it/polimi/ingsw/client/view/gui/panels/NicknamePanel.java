package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NicknamePanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private static final int BUTTON_SIZE = 175;
    private JButton sendButton;
    private JLabel stand;

    public NicknamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
        createSendButton();
    }

    public void createWaitStand() {
        ImageIcon icon = new ImageIcon("img/labels/stand.png");
        Image img = icon.getImage().getScaledInstance( 540, 540, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );
        stand = new JLabel(icon);
        stand.setOpaque(false);
        stand.setLayout(new GridBagLayout());

        add(stand, new GridBagConstraints());
    }

    public void createSendButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/send_button.png");
        Image img = icon.getImage().getScaledInstance( BUTTON_SIZE, BUTTON_SIZE, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridy = 2;
        c.gridx = 0;
        c.anchor = GridBagConstraints.SOUTH;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(0,0,-300,0);

        sendButton = new JButton(icon);
        sendButton.setOpaque(false);
        sendButton.setContentAreaFilled(false);
        sendButton.setBorderPainted(false);

        sendButton.addActionListener(this);
        stand.add(sendButton, c);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        this.panelIndex.next(this.panels);
    }
}