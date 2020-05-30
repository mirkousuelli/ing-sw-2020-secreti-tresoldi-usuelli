package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.client.view.gui.component.JWorker;

public class JBlockDecorator extends JCell {
    private final JCell origin;
    private JDecorator decoration;
    private JWorker worker;

    public JBlockDecorator(JCell origin) {
        super(origin.getXCoordinate(), origin.getYCoordinate());

        this.decoration = null;
        this.worker = null;
        this.origin = origin;

        this.setStatus(this.origin.getStatus());
    }

    public void addDecoration(JCellStatus decoration) {
        removeAll();
        this.decoration = new JDecorator(decoration);
        add(this.decoration.getComponent());

        if (worker != null)
            this.decoration.addOver(worker.getPawn().getComponent());
    }

    public void removeDecoration() {
        removeAll();
        this.decoration = null;

        if (worker != null) {
            add(worker.getPawn().getComponent());
        }

        validate();
        repaint();
    }

    public JCellStatus getDecoration() {
        if (decoration == null)
            return null;
        return this.decoration.getDecoration();
    }

    public void addWorker(JWorker worker) {
        this.worker = worker;

        if (decoration != null)
            this.decoration.addOver(this.worker.getPawn().getComponent());
        else
            add(this.worker.getPawn().getComponent());

        repaint();
        validate();
    }

    public void removeWorker() {
        if (decoration != null)
            this.decoration.removeOver();
        else
            removeAll();
        worker = null;

        repaint();
        validate();
    }

    public boolean isFree() {
        return (worker == null);
    }

    public JCellStatus getWorker() {
        return this.worker.getPawn().getDecoration();
    }

    public JWorker getJWorker() {
        return worker;
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
        if (decoration != null) {
            JCellStatus curr = decoration.getDecoration();

            if (curr.equals(JCellStatus.MOVE) || curr.equals(JCellStatus.BUILD) ||
                    curr.equals(JCellStatus.USE_POWER) || curr.equals(JCellStatus.MALUS)) {
                removeDecoration();
            }
        }
    }
}
