package it.polimi.ingsw.communication.message.payload;

public class ReducedAnswerCell extends ReducedDemandCell {

    private ReducedLevel level;
    private ReducedAction action;
    private ReducedWorker worker;

    public ReducedAnswerCell(int x, int y) {
        this(x, y , null);
    }

    public ReducedAnswerCell(int x, int y, ReducedWorker worker) {
        super(x, y);
        level = ReducedLevel.GROUND;
        action = ReducedAction.DEFAULT;
        this.worker = worker;
    }

    public ReducedAnswerCell() {}

    public ReducedLevel getLevel() {
        return level;
    }

    public ReducedAction getAction() {
        return action;
    }

    public ReducedWorker getWorker() {
        return worker;
    }

    public boolean isFree() {
        return worker == null;
    }

    public void setLevel(ReducedLevel level) {
        this.level = level;
    }

    public void setAction(ReducedAction action) {
        this.action = action;
    }

    public void setWorker(ReducedWorker worker) {
        this.worker = worker;
    }

}
