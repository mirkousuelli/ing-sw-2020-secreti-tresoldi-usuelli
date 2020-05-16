package it.polimi.ingsw.client.view.gui.button;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class JBlockDecorator extends JCell {
    private final JCell origin;
    private int current;
    protected ArrayList<JCellStatus> decoration;
    protected ArrayList<JLabel> history;

    public JBlockDecorator(JCell origin) {
        super(origin.getXCoordinate(), origin.getYCoordinate());
        this.decoration = new ArrayList<>();
        this.history = new ArrayList<>();
        this.origin = origin;
        this.current = -1;
        this.setStatus(this.origin.getStatus());
    }

    public void addDecoration(JCellStatus decoration) {
        this.current++;
        this.decoration.add(decoration);

        ImageIcon icon = new ImageIcon(decoration.getPath());
        Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        this.history.add(new JLabel(icon));
        add(this.history.get(current), new GridBagConstraints());
    }

    public void removeDecoration() {
        if (this.current >= 0) {
            remove(this.history.get(current));
            this.history.remove(current);
            this.current--;
        }
    }

    public JCellStatus getDecoration() {
        return this.decoration.get(current);
    }

    public JCell getBlock() {
        return this.origin;
    }

    public void buildUp() {
        if (((JBlock)this.origin).isTop())
            this.addDecoration(JCellStatus.DOME);
        else {
            ((JBlock)this.origin).buildUp();
            this.setStatus(this.origin.getStatus());
        }
    }

    public void reset() {
        removeAll();
    }
}
