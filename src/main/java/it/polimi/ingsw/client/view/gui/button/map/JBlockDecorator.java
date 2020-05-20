package it.polimi.ingsw.client.view.gui.button.map;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JBlockDecorator extends JCell {
    private final JCell origin;
    private int current;
    protected ArrayList<JCellStatus> decoration;
    protected ArrayList<JComponent> history;

    public JBlockDecorator(JCell origin) {
        super(origin.getXCoordinate(), origin.getYCoordinate());
        this.decoration = new ArrayList<>();
        this.history = new ArrayList<>();
        this.origin = origin;
        this.setStatus(this.origin.getStatus());
        setEnabled(false);

        this.current = 0;
        this.history.add(this);
        this.decoration.add(this.getStatus());
    }

    public void addDecoration(JCellStatus decoration) {
        if (!this.decoration.contains(decoration)) {
            this.current++;
            this.decoration.add(decoration);

            ImageIcon icon = new ImageIcon(decoration.getPath());
            Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            JLabel newDecoration = new JLabel(icon);
            newDecoration.setLayout(new GridBagLayout());
            this.history.add(newDecoration);
            this.history.get(current - 1).add(this.history.get(current), new GridBagConstraints());

            if (decoration.equals(JCellStatus.MOVE) | decoration.equals(JCellStatus.BUILD) | decoration.equals(JCellStatus.USE_POWER))
                setEnabled(true);

            validate();
            repaint();
        }
    }

    public void removeCurrentDecoration() {
        if (this.current > 0) {
            remove(this.history.get(current));
            this.history.remove(current);
            this.current--;
        }
    }

    public JCellStatus getCurrentDecoration() {
        return this.decoration.get(current);
    }

    public boolean containsDecoration(JCellStatus dec) {
        return this.decoration.contains(dec);
    }

    public void removeDecoration(JCellStatus dec) {
        int index = this.decoration.indexOf(dec);
        this.decoration.remove(dec);
        remove(this.history.remove(index));
        setEnabled(false);
        this.current--;
        validate();
        repaint();
    }

    public JCell getBlock() {
        return this.origin;
    }

    public void buildUp() {
        if (((JBlock)this.origin).isTop()) {
            this.addDecoration(JCellStatus.DOME);
        } else {
            ((JBlock)this.origin).buildUp();
            this.setStatus(this.origin.getStatus());
        }
    }

    public void reset() {
        removeAll();
    }

    public void clean() {
        JCellStatus curr;
        List<JCellStatus> toClean = new ArrayList<>();

        toClean.add(JCellStatus.MOVE);
        toClean.add(JCellStatus.BUILD);
        toClean.add(JCellStatus.USE_POWER);
        toClean.add(JCellStatus.MALUS);

        for (int i = 0; i < this.decoration.size(); i++) {
            curr = this.decoration.get(i);
            if (toClean.contains(curr)) {
                this.removeDecoration(curr);
            }
        }
    }
}
