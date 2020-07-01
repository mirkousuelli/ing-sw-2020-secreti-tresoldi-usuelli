package it.polimi.ingsw.client.view.gui.panels;

import it.polimi.ingsw.client.view.gui.GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract class that represents the main panel of the GUI and is represented by an index
 * <p>
 * It implements {@link BackgroundPanel} and is extended by panels such as {@link StartPanel}, {@link EndPanel}...
 */
public abstract class SantoriniPanel extends JPanel implements BackgroundPanel {

    private final String imgPath;
    private final Image img;

    private int scaleWidth;
    private int scaleHeight;
    protected CardLayout panelIndex;
    protected JPanel panels;

    /**
     * Constructor of the SantoriniPanel, which is built from the path of the image, its index and its panels
     *
     * @param imgPath    the path where the image is located
     * @param panelIndex the index of the panel
     * @param panels     the panels used
     */
    public SantoriniPanel(String imgPath, CardLayout panelIndex, JPanel panels) {
        this.imgPath = imgPath;
        this.panelIndex = panelIndex;
        this.panels = panels;

        this.scaleWidth = BackgroundPanel.WIDTH;
        this.scaleHeight = BackgroundPanel.HEIGHT;
        Dimension firstSize = new Dimension(scaleWidth, scaleHeight);
        int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int maxHeight = maxWidth * firstSize.height / firstSize.width;

        img = BackgroundPanel.getScaledImage(new ImageIcon(this.getClass().getResource(BackgroundPanel.BACKGROUND + this.imgPath)),
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

        scaleWidth = (int) Math.round(img.getWidth(null) * scaleFactor);
        scaleHeight = (int) Math.round(img.getHeight(null) * scaleFactor);

        Image scaled = img.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        int x = (width - scaled.getWidth(null)) / 2;
        int y = (height - scaled.getHeight(null)) / 2;

        g.drawImage(scaled, x, y, this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(scaleWidth, scaleHeight);
    }

    /**
     * Method that allows the panel to be updated with the changes. It also controls if there was a disconnection, in
     * which case is sets the interface free
     */
    public void updateFromModel() {
        //to override when needed
        ManagerPanel mg = (ManagerPanel) panels;
        GUI gui = mg.getGui();

        if (mg.evalDisconnection())
            gui.free();
    }
}
