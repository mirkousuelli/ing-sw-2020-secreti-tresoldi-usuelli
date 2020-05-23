package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChooseStarterPanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "menu.png";
    private JLabel stand;

    public ChooseStarterPanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createWaitStand();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        ;
    }
}
