package it.polimi.ingsw.client.view.gui.component;

import it.polimi.ingsw.client.view.gui.component.map.JBlockDecorator;
import it.polimi.ingsw.client.view.gui.component.map.JCell;
import it.polimi.ingsw.client.view.gui.component.map.JCellStatus;
import it.polimi.ingsw.client.view.gui.component.map.JDecorator;

public class JWorker {
    private final JDecorator pawn;

    private JCell cell;
    private int id;

    public JWorker(JCellStatus pawn, JCell cell) {
        this.pawn = new JDecorator(pawn);
        this.cell = cell;
        ((JBlockDecorator) cell).addWorker(this);
    }

    public JDecorator getPawn() {
        return pawn;
    }

    public void setPawn(JCellStatus pawn) {
        this.pawn.setDecoration(pawn);
    }

    public JCell getLocation() {
        return cell;
    }

    public void setLocation(JCell cell) {
        ((JBlockDecorator) this.cell).removeWorker();
        this.cell = cell;
        ((JBlockDecorator) this.cell).addWorker(this);
    }

    public void setCell(JCell cell) {
        this.cell = cell;
        ((JBlockDecorator) this.cell).addWorker(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
