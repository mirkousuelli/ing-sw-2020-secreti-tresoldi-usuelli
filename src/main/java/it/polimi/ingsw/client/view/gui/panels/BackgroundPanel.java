package it.polimi.ingsw.client.view.gui.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public interface BackgroundPanel {
    String BACKGROUND = "img/background/";
    int WIDTH = 1067;
    int HEIGHT = 600;

    static ImageIcon getScaledImage(ImageIcon srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg.getImage(), 0, 0, w, h, null);
        g2.dispose();

        return new ImageIcon(resizedImg);
    }

    static double getScaleFactor(int iMasterSize, int iTargetSize) {
        double dScale = 1;

        if (iMasterSize > iTargetSize)
            dScale = (double) iTargetSize / (double) iMasterSize;
        else
            dScale = (double) iTargetSize / (double) iMasterSize;

        return dScale;
    }

    static double getScaleFactorToFit(Dimension original, Dimension toFit) {
        double dScale = 1d;

        if (original != null && toFit != null) {
            double dScaleWidth = getScaleFactor(original.width, toFit.width);
            double dScaleHeight = getScaleFactor(original.height, toFit.height);

            dScale = Math.min(dScaleHeight, dScaleWidth);
        }

        return dScale;
    }
}
