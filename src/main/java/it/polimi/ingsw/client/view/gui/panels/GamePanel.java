package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.component.JGame;
import it.polimi.ingsw.client.view.gui.component.JPlayer;
import it.polimi.ingsw.client.view.gui.component.deck.JCard;
import it.polimi.ingsw.client.view.gui.component.map.*;
import it.polimi.ingsw.server.model.cards.gods.God;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class GamePanel extends SantoriniPanel implements ActionListener {
    private static final String imgPath = "map.png";
    private JGame game;
    private JPanel right;
    private JPanel lobby;
    private JPanel card;
    private JCard cardButton;
    private JPanel left;
    //private JPanel firstMalus;
    //private JPanel secondMalus;
    private JLabel lobbyStand;
    private JLabel[] player;
    private JButton quitButton;

    public GamePanel(CardLayout panelIndex, JPanel panels, JGame game) {
        super(imgPath, panelIndex, panels);

        this.game = game;
        //player = new JLabel[3];

        createRightSection();
        //createQuitButton();
        createCardSection();

        createMap();

        createLeftSection();
        for (JPlayer p : this.game.getPlayerList())
            createFirstMalusSection();
        //createSecondMalusSection();
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
        mapCon.insets = new Insets(70,55,85,70);

        add(this.game.getJMap(), mapCon);
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
        right.setLayout(new GridBagLayout());

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
        GridBagConstraints cardCon = new GridBagConstraints();
        cardCon.insets = new Insets(0,20,0,0);

        card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        card.setVisible(true);
        right.add(card, cardCon);

        cardButton = new JCard(God.APOLLO);
        cardButton.addActionListener(this);
        card.add(cardButton, new GridBagConstraints());
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
        JPanel firstMalus = new JPanel(new GridBagLayout());
        firstMalus.setOpaque(false);
        firstMalus.setVisible(true);
        left.add(firstMalus);

        ImageIcon icon_1 = new ImageIcon("img/cards/persephone/malus.png");
        Image img_1 = icon_1.getImage().getScaledInstance( 170, 280, Image.SCALE_SMOOTH);
        icon_1 = new ImageIcon( img_1 );
        JLabel malus_1 = new JLabel(icon_1);
        firstMalus.add(malus_1, new GridBagConstraints());
    }

    /*void createSecondMalusSection() {
        secondMalus = new JPanel(new GridBagLayout());
        secondMalus.setOpaque(false);
        secondMalus.setVisible(true);
        left.add(secondMalus);

        ImageIcon icon_2 = new ImageIcon("img/cards/athena/malus.png");
        Image img_2 = icon_2.getImage().getScaledInstance( 170, 280, Image.SCALE_SMOOTH);
        icon_2 = new ImageIcon( img_2 );
        JLabel malus_2 = new JLabel(icon_2);
        secondMalus.add(malus_2, new GridBagConstraints());
    }*/


    public void setPossibleMove(List<JCell> where) {
        this.game.getJMap().setPossibleMove(where);
        cardButton.applyNormal();
    }

    public void setPossibleBuild(List<JCell> where) {
        this.game.getJMap().setPossibleBuild(where);
        cardButton.applyNormal();
    }

    public void setPossibleUsePowerMove(List<JCell> where) {
        this.game.getJMap().setPossibleUsePowerMove(where);
        cardButton.applyPower();
    }

    public void setPossibleUsePowerBuild(List<JCell> where) {
        this.game.getJMap().setPossibleUsePowerBuild(where);
        cardButton.applyPower();
    }

    public void setPossibleMalus(List<JCell> where) {
        this.game.getJMap().setPossibleMalus(where);
        cardButton.applyNormal();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JCard src = (JCard)e.getSource();

        switch (src.getName()) {
            case "card":
                if (this.game.getJMap().isPowerActive()) {
                    this.game.getJMap().hidePowerCells();
                    cardButton.applyPower();
                }
                break;

            case "power":
                if (this.game.getJMap().isPowerActive()) {
                    this.game.getJMap().showPowerCells();
                    cardButton.applyNormal();
                }
                break;
        }
    }
}