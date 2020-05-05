package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public abstract class SantoriniPanel extends JPanel implements BackgroundPanel {
    private final String imgPath;
    private final Image img;

    public SantoriniPanel(String imgPath) {
        this.imgPath = imgPath;
        Dimension firstSize = new Dimension(BackgroundPanel.WIDTH, BackgroundPanel.HEIGHT);
        int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int maxHeight = maxWidth * firstSize.height / firstSize.width;

        img = BackgroundPanel.getScaledImage(new ImageIcon(BackgroundPanel.BACKGROUND + this.imgPath),
                maxWidth, maxHeight).getImage();

        setLayout(new GridBagLayout());
        setPreferredSize(firstSize);
        setSize(firstSize);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        double scaleFactor = Math.min(1d, BackgroundPanel.getScaleFactorToFit(
                new Dimension(img.getWidth(null), img.getHeight(null)), getSize()));

        int scaleWidth = (int) Math.round(img.getWidth(null) * scaleFactor);
        int scaleHeight = (int) Math.round(img.getHeight(null) * scaleFactor);

        Image scaled = img.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        int x = (width - scaled.getWidth(null)) / 2;
        int y = (height - scaled.getHeight(null)) / 2;

        g.drawImage(scaled, x, y, this);
    }
}
