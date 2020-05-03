package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.tools.ImageTools;

import javax.swing.*;
import java.awt.*;

public class EndPanel extends JPanel implements SantoriniPanel {
    public static final String VICTORY = "victory.png";
    public static final String DEFEAT = "defeat.png";
    public static final String LOST = "lost.png";
    private final Image img;
    private String type;

    public EndPanel(String type) {
        this.type = type;
        img = ImageTools.getScaledImage(new ImageIcon(SantoriniPanel.BACKGROUND + type),
                SantoriniPanel.WIDTH, SantoriniPanel.HEIGHT).getImage();
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setOpaque(false);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(img, 0, 0, null);
    }
}
