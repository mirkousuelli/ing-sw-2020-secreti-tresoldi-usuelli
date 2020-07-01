package it.polimi.ingsw.client.view.gui.component.map;

/**
 * Class that represents the block in the GUI.
 * <p>
 * It extends {@link JCell}
 */
public class JBlock extends JCell {

    /**
     * Constructor of the JBlock, given its coordinates. It sets its status to none.
     *
     * @param x x-coordinate of this block
     * @param y y-coordinate of this block
     */
    public JBlock(int x, int y) {
        super(x, y);
        setStatus(JCellStatus.NONE);
    }

    /**
     * Method that builds on this block with the proper element
     */
    public void buildUp() {
        setStatus(getStatus().getNext());
    }

    public boolean isTop() {
        return getStatus().equals(JCellStatus.TOP);
    }
}
