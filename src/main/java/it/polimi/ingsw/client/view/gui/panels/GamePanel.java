package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GamePanel extends SantoriniPanel {
    private static final String imgPath = "map.png";
    private final static int DIM = 5;
    private JPanel map;
    private JButton[][] cellButton;
    private JPanel right;
    private JPanel lobby;
    private JPanel card;
    private JPanel left;
    private JPanel firstMalus;
    private JPanel secondMalus;

    public GamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        createRightSection();
        createMap();
        createLeftSection();
    }

    void createMap() {
        GridBagConstraints mapCon = new GridBagConstraints();

        mapCon.anchor = GridBagConstraints.CENTER;
        mapCon.gridx = 1;
        mapCon.gridy = 0;

        mapCon.gridwidth = 1;
        mapCon.gridheight = 2;

        mapCon.weightx = 0.5;
        mapCon.weighty = 1;
        mapCon.fill = GridBagConstraints.BOTH;
        mapCon.insets = new Insets(70,10,85,10);
        map = new JPanel(new GridBagLayout());
        map.setOpaque(false);
        map.setVisible(true);

        cellButton = new JButton[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = j;
                c.gridy = i;
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 1;
                c.weightx = 1;
                cellButton[i][j] = new JButton("(" + i + ", " + j + ")");
                map.add(cellButton[i][j], c);
            }
        }
        map.setBorder(new EmptyBorder(0,20,0,10));
        add(map, mapCon);
    }

    void createRightSection() {
        GridBagConstraints rightCon = new GridBagConstraints();

        rightCon.anchor = GridBagConstraints.WEST;
        rightCon.gridx = 0;
        rightCon.gridy = 0;
        rightCon.gridwidth = 1;
        rightCon.gridheight = 2;
        rightCon.weightx = 2;
        rightCon.weighty = 1;
        rightCon.fill = GridBagConstraints.BOTH;

        right = new JPanel(new GridLayout(2,1));
        right.setVisible(true);
        right.setOpaque(true);

        lobby = new JPanel(new BorderLayout());
        lobby.setBackground(Color.YELLOW);
        lobby.setVisible(true);
        right.add(lobby);

        card = new JPanel(new BorderLayout());
        card.setBackground(Color.MAGENTA);
        card.setVisible(true);
        right.add(card);

        add(right, rightCon);
    }

    void createLeftSection() {
        GridBagConstraints leftCon = new GridBagConstraints();

        leftCon.anchor = GridBagConstraints.EAST;
        leftCon.gridx = 2;
        leftCon.gridy = 0;
        leftCon.gridwidth = 1;
        leftCon.gridheight = 2;
        leftCon.weightx = 2;
        leftCon.weighty = 1;
        leftCon.fill = GridBagConstraints.BOTH;

        left = new JPanel(new GridLayout(2,1));
        left.setVisible(true);
        left.setOpaque(true);

        firstMalus = new JPanel(new BorderLayout());
        firstMalus.setBackground(Color.BLUE);
        firstMalus.setVisible(true);
        left.add(firstMalus);

        secondMalus = new JPanel(new BorderLayout());
        secondMalus.setBackground(Color.RED);
        secondMalus.setVisible(true);
        left.add(secondMalus);

        add(left, leftCon);
    }
}