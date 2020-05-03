package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.tools.ImageTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartPanel extends JPanel implements SantoriniPanel {
    private final String imgPath = "intro.png";
    private final Image img;

    public StartPanel() {
        img = ImageTools.getScaledImage(new ImageIcon(SantoriniPanel.BACKGROUND + imgPath),
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
