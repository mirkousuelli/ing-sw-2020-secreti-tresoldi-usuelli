package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GamePanel extends SantoriniPanel {
    private static final String imgPath = "map.png";
    private final static int DIM = 5;
    private JPanel map;
    private JButton[][] cellButton;
    private JPanel player;
    private JPanel lobby;
    private JPanel card;
    private JPanel malus;
    private JPanel firstMalus;
    private JPanel secondMalus;

    public GamePanel() {
        super(imgPath);

        createMap();
        createRightSection();
        createLeftSection();
    }

    void createMap() {
        map = new JPanel(new GridLayout(DIM, DIM, DIM, DIM));
        map.setOpaque(false);
        map.setVisible(true);

        cellButton = new JButton[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                cellButton[i][j] = new JButton("(" + i + ", " + j + ")");
                cellButton[i][j].setSize(new Dimension(40,40));
                map.add(cellButton[i][j]);
            }
        }

        map.setBorder(new EmptyBorder(72,50,85,45));
        add(map);
    }

    void createRightSection() {
        player = new JPanel(new GridLayout(2,1));
        player.setVisible(true);
        player.setPreferredSize(new Dimension((int) (getWidth() * 0.25), getHeight()));
        add(player);

        lobby = new JPanel(new BorderLayout());
        lobby.setBackground(Color.YELLOW);
        lobby.setVisible(true);
        lobby.setPreferredSize(new Dimension(player.getWidth(), getHeight()/2));
        add(lobby);

        card = new JPanel(new BorderLayout());
        card.setBackground(Color.MAGENTA);
        card.setVisible(true);
        card.setPreferredSize(new Dimension(player.getWidth(), getHeight()/2));
        player.add(card);
    }

    void createLeftSection() {
        malus = new JPanel(new GridLayout(2,1));
        malus.setVisible(true);
        malus.setPreferredSize(new Dimension((int) (getWidth() * 0.25), getHeight()));
        add(malus);

        firstMalus = new JPanel(new BorderLayout());
        firstMalus.setBackground(Color.BLUE);
        firstMalus.setVisible(true);
        firstMalus.setPreferredSize(new Dimension(malus.getWidth(), getHeight()/2));
        malus.add(firstMalus);

        secondMalus = new JPanel(new BorderLayout());
        secondMalus.setBackground(Color.RED);
        secondMalus.setVisible(true);
        secondMalus.setPreferredSize(new Dimension(malus.getWidth(), getHeight()/2));
        malus.add(secondMalus);
    }
}