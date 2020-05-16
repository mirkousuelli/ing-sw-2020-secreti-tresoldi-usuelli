package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.button.JBlock;
import it.polimi.ingsw.client.view.gui.button.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.button.JCell;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends SantoriniPanel {
    private static final String imgPath = "map.png";
    private final static int DIM = 5;
    private JPanel map;
    private JCell[][] cellButton;
    private JPanel right;
    private JPanel lobby;
    private JPanel card;
    private JPanel left;
    private JPanel firstMalus;
    private JPanel secondMalus;
    private JLabel lobbyStand;
    private JLabel[] player;
    private JButton quitButton;

    public GamePanel(CardLayout panelIndex, JPanel panels) {
        super(imgPath, panelIndex, panels);

        player = new JLabel[3];

        createRightSection();
        createLobbySection();
        createLobbyPlayers();
        createQuitButton();
        createCardSection();

        createMap();

        createLeftSection();
        createFirstMalusSection();
        createSecondMalusSection();
    }

    void createMap() {
        GridBagConstraints mapCon = new GridBagConstraints();

        mapCon.anchor = GridBagConstraints.CENTER;
        mapCon.gridx = 1;
        mapCon.gridy = 0;

        mapCon.gridwidth = 1;
        mapCon.gridheight = 2;

        mapCon.weightx = 0.1;
        mapCon.weighty = 0.1;
        mapCon.fill = GridBagConstraints.BOTH;
        mapCon.insets = new Insets(70,10,85,70);
        map = new JPanel(new GridBagLayout());
        map.setOpaque(false);
        map.setVisible(true);

        cellButton = new JCell[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                GridBagConstraints c = new GridBagConstraints();
                c.gridx = j;
                c.gridy = i;
                c.fill = GridBagConstraints.BOTH;
                c.weighty = 1;
                c.weightx = 1;
                cellButton[i][j] = new JBlockDecorator(new JBlock(i, j));
                cellButton[i][j].addActionListener(e -> ((JBlockDecorator)e.getSource()).buildUp());
                map.add(cellButton[i][j], c);
            }
        }
        map.setBorder(new EmptyBorder(0,25,0,10));
        add(map, mapCon);
    }

    void createRightSection() {
        GridBagConstraints rightCon = new GridBagConstraints();

        rightCon.anchor = GridBagConstraints.WEST;
        rightCon.gridx = 0;
        rightCon.gridy = 0;
        rightCon.gridwidth = 1;
        rightCon.gridheight = 2;
        rightCon.weightx = 0.1;
        rightCon.weighty = 1;
        rightCon.fill = GridBagConstraints.BOTH;

        right = new JPanel(new GridLayout(2,1));
        right.setVisible(true);
        right.setOpaque(false);

        add(right, rightCon);
    }

    void createLobbySection() {
        lobby = new JPanel(new GridBagLayout());
        lobby.setVisible(true);
        lobby.setOpaque(false);
        right.add(lobby);

        ImageIcon icon_1 = new ImageIcon("img/labels/lobby.png");
        Image img_1 = icon_1.getImage().getScaledInstance( 220, 280, Image.SCALE_SMOOTH);
        icon_1 = new ImageIcon( img_1 );
        lobbyStand = new JLabel(icon_1);
        lobbyStand.setLayout(new GridBagLayout());
        lobby.add(lobbyStand, new GridBagConstraints());
    }

    void createLobbyPlayers() {
        for (int i = 0; i < 3; i++) {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = i;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 0f;
            c.weightx = 1;
            c.insets = (i == 0) ? new Insets(50,0,0,0) : new Insets(0,0,0,0);
            ImageIcon icon = new ImageIcon("img/workers/worker_" + (i + 1) + "/tag.png");
            Image img = icon.getImage().getScaledInstance( 200, 50, Image.SCALE_SMOOTH);
            icon = new ImageIcon( img );
            player[i] = new JLabel(icon);
            lobbyStand.add(player[i], c);
        }
    }

    void createQuitButton() {
        GridBagConstraints c = new GridBagConstraints();

        ImageIcon icon = new ImageIcon("img/buttons/quit_button.png");
        Image img = icon.getImage().getScaledInstance( 100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon( img );

        c.gridx = 0;
        c.gridy = 3;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0f;
        c.weighty = 0f;

        quitButton = new JButton(icon);
        quitButton.setOpaque(false);
        quitButton.setContentAreaFilled(false);
        quitButton.setBorderPainted(false);
        //quitButton.setBorder(new EmptyBorder(0,0,80,0));

        //playButton.addActionListener(this);
        lobbyStand.add(quitButton, c);
    }

    void createCardSection() {
        card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        card.setVisible(true);
        right.add(card);

        ImageIcon icon_2 = new ImageIcon("img/cards/apollo/apollo.png");
        Image img_2 = icon_2.getImage().getScaledInstance( 170, 280, Image.SCALE_SMOOTH);
        icon_2 = new ImageIcon( img_2 );
        JLabel god = new JLabel(icon_2);
        card.add(god, new GridBagConstraints());
    }

    void createLeftSection() {
        GridBagConstraints leftCon = new GridBagConstraints();

        leftCon.anchor = GridBagConstraints.EAST;
        leftCon.gridx = 2;
        leftCon.gridy = 0;
        leftCon.gridwidth = 1;
        leftCon.gridheight = 2;
        leftCon.weightx = 0.1;
        leftCon.weighty = 1;
        leftCon.fill = GridBagConstraints.BOTH;

        left = new JPanel(new GridLayout(2,1));
        left.setVisible(true);
        left.setOpaque(false);

        add(left, leftCon);
    }

    void createFirstMalusSection() {
        firstMalus = new JPanel(new GridBagLayout());
        firstMalus.setOpaque(false);
        firstMalus.setVisible(true);
        left.add(firstMalus);

        ImageIcon icon_1 = new ImageIcon("img/cards/malus/malus_athena.png");
        Image img_1 = icon_1.getImage().getScaledInstance( 170, 280, Image.SCALE_SMOOTH);
        icon_1 = new ImageIcon( img_1 );
        JLabel malus_1 = new JLabel(icon_1);
        firstMalus.add(malus_1, new GridBagConstraints());
    }

    void createSecondMalusSection() {
        secondMalus = new JPanel(new GridBagLayout());
        secondMalus.setOpaque(false);
        secondMalus.setVisible(true);
        left.add(secondMalus);

        ImageIcon icon_2 = new ImageIcon("img/cards/malus/malus_persephone.png");
        Image img_2 = icon_2.getImage().getScaledInstance( 170, 280, Image.SCALE_SMOOTH);
        icon_2 = new ImageIcon( img_2 );
        JLabel malus_2 = new JLabel(icon_2);
        secondMalus.add(malus_2, new GridBagConstraints());
    }

    /*@Override
    public void actionPerformed(ActionEvent e) {
        ((JBlockDecorator)e.getSource()).buildUp();
        /*ImageIcon icon = new ImageIcon("img/blocks/top.png");
        Image img = icon.getImage().getScaledInstance( cellButton[0][0].getWidth(), cellButton[0][0].getHeight(), Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);
        cellButton[0][0].setIcon(icon);

        ImageIcon icon_2 = new ImageIcon("img/blocks/move.png");
        Image img_2 = icon_2.getImage().getScaledInstance(cellButton[0][0].getWidth(), cellButton[0][0].getHeight(), Image.SCALE_SMOOTH);
        icon_2 = new ImageIcon(img_2);
        cellButton[0][0].setLayout(new GridBagLayout());
        cellButton[0][0].add(new JLabel(icon_2), new GridBagConstraints());
    }*/
}