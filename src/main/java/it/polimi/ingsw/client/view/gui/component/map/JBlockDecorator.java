package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.client.view.gui.component.JWorker;

import javax.swing.*;
import java.awt.*;

public class JBlockDecorator extends JCell {
    private final JCell origin;
    private JDecorator decoration;
    private JWorker worker;

    public JBlockDecorator(JCell origin) {
        super(origin.getXCoordinate(), origin.getYCoordinate());
        this.decoration = new JDecorator();
        this.worker = new JWorker();
        this.origin = origin;

        this.setStatus(this.origin.getStatus());
        this.decoration.setDecoration(JCellStatus.NONE);
    }

    public void addDecoration(JCellStatus decoration) {
        if (!this.decoration.getDecoration().equals(decoration)) {
            removeAll();
            this.decoration.setDecoration(decoration);

            ImageIcon icon = new ImageIcon(decoration.getPath());
            Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);

            JLabel newDecoration = new JLabel(icon);
            newDecoration.setLayout(new GridBagLayout());
            this.decoration.setComponent(newDecoration);
            add(this.decoration.getComponent());
            if (!worker.getPawn().getDecoration().equals(JCellStatus.NONE))
                addWorkerOnTop();

            repaint();
            validate();
        }
    }

    public void removeDecoration() {
        this.decoration.setDecoration(JCellStatus.NONE);
        removeAll();
        addWorker(worker);

        validate();
        repaint();
    }

    public JCellStatus getDecoration() {
        return this.decoration.getDecoration();
    }

    public JDecorator getDecorator() {
        return this.decoration;
    }

    public void addWorker(JWorker worker) {
        this.worker = worker;

        ImageIcon icon = new ImageIcon(worker.getPawn().getDecoration().getPath());
        Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        JLabel newWorker = new JLabel(icon);
        newWorker.setLayout(new GridBagLayout());
        this.worker.getPawn().setComponent(newWorker);
        add(this.worker.getPawn().getComponent());

        repaint();
        validate();
    }

    private void addWorkerOnTop() {
        ImageIcon icon = new ImageIcon(worker.getPawn().getDecoration().getPath());
        Image img = icon.getImage().getScaledInstance(DIMENSION, DIMENSION, Image.SCALE_SMOOTH);
        icon = new ImageIcon(img);

        JLabel newWorker = new JLabel(icon);
        newWorker.setLayout(new GridBagLayout());
        this.worker.getPawn().setComponent(newWorker);
        this.decoration.getComponent().add(this.worker.getPawn().getComponent());

        repaint();
        validate();
    }

    public void removeWorker() {
        removeAll();
        this.worker = new JWorker();

        repaint();
        validate();
    }

    public JCellStatus getWorker() {
        return this.worker.getPawn().getDecoration();
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

    public void clean() {
        JCellStatus curr = decoration.getDecoration();

        if (curr.equals(JCellStatus.MOVE) || curr.equals(JCellStatus.BUILD) ||
                curr.equals(JCellStatus.USE_POWER) || curr.equals(JCellStatus.MALUS)) {
            removeDecoration();
            removeAll();
        }
    }
}
