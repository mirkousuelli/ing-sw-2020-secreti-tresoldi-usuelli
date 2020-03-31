package it.polimi.ingsw.model.cards.powers.tags;

public class Constraint {
    private int numberOfAdditional; // Numero di volte che puoi usare il potere nello stesso turno
    private boolean sameCell; // se DEVO applicare il potere sulla cella su cui ho mosso/costruito nelle stesso turno
    private boolean notSameCell; // se NON POSSO applicare il potere sulla cella che ho mosso/costruito nelle stesso turno
    private boolean perimCell; // se DEVO applicare il potere sulle celle perimetrali
    private boolean notPerimCell; // se NON POSSO applicare il potere sulle celle perimetrali
    private boolean underItself; // se DEVO applicare il potere sulla cella su cui ho mosso/costruito nella stessa celle in cui mi trovo
    //notUnderItself non c'è perché normalente lo dicono già le regole base, non ha senso

    public Constraint() {}

    public int getNumberOfAdditional() {
        return numberOfAdditional;
    }

    public void setNumberOfAdditional(int numberOfAdditional) {
        this.numberOfAdditional = numberOfAdditional;
    }

    public boolean isSameCell() {
        return sameCell;
    }

    public void setSameCell(boolean sameCell) {
        this.sameCell = sameCell;
    }

    public boolean isNotSameCell() {
        return notSameCell;
    }

    public void setNotSameCell(boolean notSameCell) {
        this.notSameCell = notSameCell;
    }

    public boolean isPerimCell() {
        return perimCell;
    }

    public void setPerimCell(boolean perimCell) {
        this.perimCell = perimCell;
    }

    public boolean isNotPerimCell() {
        return notPerimCell;
    }

    public void setNotPerimCell(boolean notPerimCell) {
        this.notPerimCell = notPerimCell;
    }

    public boolean isUnderItself() {
        return underItself;
    }

    public void setUnderItself(boolean underItself) {
        this.underItself = underItself;
    }
}
