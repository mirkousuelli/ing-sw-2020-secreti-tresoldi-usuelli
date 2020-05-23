package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;
import it.polimi.ingsw.client.view.gui.component.map.JDecorator;

public class JWorker {
    private JDecorator pawn;
    private JCell cell;

    public JWorker() {
        pawn = new JDecorator();
        pawn.setDecoration(JCellStatus.NONE);
    }

    public JWorker(JDecorator pawn, JCell cell) {
        this.pawn = pawn;
        this.cell = cell;
        ((JBlockDecorator) cell).addWorker(this);
    }
    public JDecorator getPawn() {
        return pawn;
    }

    public void setPawn(JDecorator pawn) {
        this.pawn = pawn;
    }

    public JCell getLocation() {
        return cell;
    }

    public void setLocation(JCell cell) {
        ((JBlockDecorator) this.cell).removeWorker();
        this.cell = cell;
        ((JBlockDecorator) this.cell).addWorker(this);
    }
}
