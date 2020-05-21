package it.polimi.ingsw.client.view.gui.component.map;

public class JBlock extends JCell {

    public JBlock(int x, int y) {
        super(x, y);
        this.setStatus(JCellStatus.NONE);
    }

    public void buildUp() {
        this.setStatus(this.getStatus().getNext());
    }

    public boolean isTop() {
        return this.getStatus().equals(JCellStatus.TOP);
    }
}
