package it.polimi.ingsw.server.model.cards.powers.tags;

public class Constraints {
    private int numberOfAdditional;
    private boolean sameCell;
    private boolean notSameCell;
    private boolean perimCell;
    private boolean notPerimCell;
    private boolean underItself;

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
