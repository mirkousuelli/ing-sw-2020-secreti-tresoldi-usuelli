package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.ClientModel;
import it.polimi.ingsw.client.view.gui.GUI;

import javax.swing.*;
import java.awt.*;

public abstract class SantoriniPanel extends JPanel implements BackgroundPanel {
    private final String imgPath;
    private final Image img;
    private int scaleWidth;
    private int scaleHeight;
    protected CardLayout panelIndex;
    protected JPanel panels;

    public SantoriniPanel(String imgPath, CardLayout panelIndex, JPanel panels) {
        this.imgPath = imgPath;
        this.panelIndex = panelIndex;
        this.panels = panels;

        this.scaleWidth = BackgroundPanel.WIDTH;
        this.scaleHeight = BackgroundPanel.HEIGHT;
        Dimension firstSize = new Dimension(scaleWidth, scaleHeight);
        int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int maxHeight = maxWidth * firstSize.height / firstSize.width;

        img = BackgroundPanel.getScaledImage(new ImageIcon(BackgroundPanel.BACKGROUND + this.imgPath),
                maxWidth, maxHeight).getImage();

        setLayout(new GridBagLayout());
        setPreferredSize(firstSize);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        double scaleFactor = Math.min(1d, BackgroundPanel.getScaleFactorToFit(
                new Dimension(img.getWidth(null), img.getHeight(null)), getSize()));

        this.scaleWidth = (int) Math.round(img.getWidth(null) * scaleFactor);
        this.scaleHeight = (int) Math.round(img.getHeight(null) * scaleFactor);

        Image scaled = img.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        int x = (width - scaled.getWidth(null)) / 2;
        int y = (height - scaled.getHeight(null)) / 2;

        g.drawImage(scaled, x, y, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.scaleWidth, this.scaleHeight);
    }

    public void updateFromModel() {}
}
