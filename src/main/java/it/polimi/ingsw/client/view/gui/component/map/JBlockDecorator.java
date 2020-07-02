package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.client.view.gui.component.JWorker;

/**
 * Class that represents the block decorator in the GUI.
 * <p>
 * It contains the cell, the decorator and the worker.
 * <p>
 * It extends {@link JCell}
 */
public class JBlockDecorator extends JCell {

    private final JCell origin;
    private JDecorator decoration;
    private JWorker worker;

    /**
     * Constructor of the JBlockDecorator. It sets decoration and the worker to null and the cell to the given one.
     *
     *
     * @param origin
     */
    public JBlockDecorator(JCell origin) {
        super(origin.getXCoordinate(), origin.getYCoordinate());

        this.decoration = null;
        this.worker = null;
        this.origin = origin;

        this.setStatus(this.origin.getStatus());
    }

    /**
     * Method that adds the given decoration to the block. It removes all components from this and then adds the given
     * decoration.
     *
     * @param decoration the type of decoration added to the cell
     */
    public void addDecoration(JCellStatus decoration) {
        removeAll();
        this.decoration = new JDecorator(decoration);
        add(this.decoration.getComponent());

        if (worker != null)
            this.decoration.addOver(worker.getPawn().getComponent());
    }

    /**
     * Method that removes decoration from the block decorator
     */
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

    /**
     * Method that adds the given worker to the block decorator
     *
     * @param worker the worker that is added
     */
    public void addWorker(JWorker worker) {
        this.worker = worker;

        if (decoration != null)
            this.decoration.addOver(this.worker.getPawn().getComponent());
        else
            add(this.worker.getPawn().getComponent());

        validate();
        repaint();
    }

    /**
     * Method that removes eventual worker from the block decorator
     */
    public void removeWorker() {
        if (decoration != null)
            this.decoration.removeOver();
        else
            removeAll();
        worker = null;

        validate();
        repaint();
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

    /**
     * Method that builds the correct block on top of the block decorator. When it is top it adds a dome
     */
    public void buildUp() {
        if (((JBlock) this.origin).isTop()) {
            this.addDecoration(JCellStatus.DOME);
        } else {
            ((JBlock) this.origin).buildUp();
            this.setStatus(this.origin.getStatus());
        }
    }

    /**
     * Method that removes any decoration from the block decorator.
     */
    public void clean() {
        if (decoration != null) {
            JCellStatus curr = decoration.getDecoration();

            if (curr.equals(JCellStatus.MOVE) || curr.equals(JCellStatus.BUILD) ||
                    curr.equals(JCellStatus.USE_POWER) || curr.equals(JCellStatus.MALUS)) {
                removeDecoration();
            }
        }
    }

    @Override
    void clear() {
        super.clear();

        origin.status = JCellStatus.NONE;
        setStatus(JCellStatus.NONE);

        if (!isFree())
            removeWorker();

        removeDecoration();

        validate();
        repaint();
    }
}
