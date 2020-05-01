package it.polimi.ingsw.client.view.gui.windows;

import it.polimi.ingsw.client.view.gui.tools.ImageTools;

import javax.swing.*;
import java.awt.*;

public class StartWindow extends JFrame implements Window {
    private final Image BACKGROUND = new ImageIcon("img/background/intro.png").getImage();

    public StartWindow(String title) {
        super(title);
        this.applyBackground();
    }

    @Override
    public void applyBackground() {
        ImageIcon img = ImageTools.getScaledImage(new ImageIcon(BACKGROUND), 1024, 576);
        //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        //BackgroundPanel panel = new BackgroundPanel(BACKGROUND, BackgroundPanel.ACTUAL, 1.0f, 0.5f);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Another way
        setLayout(new BorderLayout());
        setContentPane(new JLabel(img));
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
