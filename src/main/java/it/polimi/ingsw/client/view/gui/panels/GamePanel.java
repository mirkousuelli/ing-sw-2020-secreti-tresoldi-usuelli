package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GamePanel extends SantoriniPanel {
    private static final String imgPath = "map.png";
    private JPanel map;
    private JButton[][] cellButton;
    private final static int DIM = 5;

    public GamePanel() {
        super(imgPath);

        map = new JPanel(new GridLayout(DIM, DIM));
        map.setOpaque(false);
        map.setBorder(new EmptyBorder(10,100,22,20));
        add(map);

        cellButton = new JButton[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                cellButton[i][j] = new JButton("(" + i + ", " + j + ")");
                cellButton[i][j].setPreferredSize(new Dimension(90,90));
                map.add(cellButton[i][j]);
            }
        }
    }
}