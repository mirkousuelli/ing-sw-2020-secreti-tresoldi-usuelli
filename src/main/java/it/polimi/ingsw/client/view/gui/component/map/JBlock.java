package it.polimi.ingsw.client.view.gui.component.map;

public class JBlock extends JCell {

    public JBlock(int x, int y) {
        super(x, y);
        setStatus(JCellStatus.NONE);
    }

    public void buildUp() {
        setStatus(getStatus().getNext());
    }

    public boolean isTop() {
        return getStatus().equals(JCellStatus.TOP);
    }
}
