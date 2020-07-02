package it.polimi.ingsw.client.view.gui.component.map;

import javax.swing.*;
import java.awt.*;

/**
 * Abstract class that represents the cell and is developed deeper by {@link JBlock} and {@link JBlockDecorator}
 * <p>
 * It contains its coordinates, dimension and status.
 */
public abstract class JCell extends JButton {
    protected final int x_coo;
    protected final int y_coo;
    protected static final int DIMENSION = 80;
    protected JCellStatus status;

    /**
     * Constructor of the cell
     *
     * @param x x-coordinate of the cell
     * @param y y-coordinate og the cell
     */
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

    /**
     * Method that clears this cell by setting its status back to {@code NONE}
     */
    void clear() {
        status = JCellStatus.NONE;

        validate();
        repaint();
    }
}
