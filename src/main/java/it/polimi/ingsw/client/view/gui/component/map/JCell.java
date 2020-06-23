package it.polimi.ingsw.client.view.gui.component.map;

import javax.swing.*;
import java.awt.*;

public abstract class JCell extends JButton {
    protected final int x_coo;
    protected final int y_coo;
    protected static final int DIMENSION = 80;
    protected JCellStatus status;

    JCell(int x, int y) {
        super();
        this.x_coo = x;
        this.y_coo = y;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setPreferredSize(new Dimension(DIMENSION, DIMENSION));
        setLayout(new GridBagLayout());
        setName("cell");
    }

    public JCellStatus getStatus() {
        return this.status;
    }

    public void setStatus(JCellStatus status) {
        this.status = status;
        ImageIcon icon;

        if (status.getPath() != null) {
            icon = new ImageIcon(this.getClass().getResource(status.getPath()));
            Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
            setIcon(icon);
        }
    }

    public int getXCoordinate() {
        return this.x_coo;
    }

    public int getYCoordinate() {
        return this.y_coo;
    }

    void clear() {
        status = JCellStatus.NONE;

        repaint();
        validate();
    }
}
