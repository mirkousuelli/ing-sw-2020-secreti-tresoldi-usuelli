package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;

public class NicknamePanel extends JPanel implements SantoriniPanel {
    private final String imgPath = "menu.png";
    private final String SUBMIT = "SUBMIT";
    private final String NICKNAME = "Nickname";
    private Image img;
    private JButton submitButton;
    private JTextField nicknameField;

    public NicknamePanel() {
        setUp();
        createNicknameField();
        createSubmitButton();
    }

    private void setUp() {
        Dimension firstSize = new Dimension(SantoriniPanel.WIDTH, SantoriniPanel.HEIGHT);
        int maxWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int maxHeight = maxWidth * firstSize.height / firstSize.width;

        img = SantoriniPanel.getScaledImage(new ImageIcon(SantoriniPanel.BACKGROUND + imgPath),
                maxWidth, maxHeight).getImage();

        setLayout(new GridBagLayout());
        setPreferredSize(firstSize);
        setSize(firstSize);
        setOpaque(false);
    }

    private void createSubmitButton() {
        submitButton = new JButton(SUBMIT);
        add(submitButton);
    }

    private void createNicknameField() {
        nicknameField = new JTextField(10);
        add(nicknameField);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        double scaleFactor = Math.min(1d, SantoriniPanel.getScaleFactorToFit(
                new Dimension(img.getWidth(null), img.getHeight(null)), getSize()));

        int scaleWidth = (int) Math.round(img.getWidth(null) * scaleFactor);
        int scaleHeight = (int) Math.round(img.getHeight(null) * scaleFactor);

        Image scaled = img.getScaledInstance(scaleWidth, scaleHeight, Image.SCALE_SMOOTH);

        int width = getWidth() - 1;
        int height = getHeight() - 1;

        int x = (width - scaled.getWidth(this)) / 2;
        int y = (height - scaled.getHeight(this)) / 2;

        g.drawImage(scaled, x, y, this);
    }
}