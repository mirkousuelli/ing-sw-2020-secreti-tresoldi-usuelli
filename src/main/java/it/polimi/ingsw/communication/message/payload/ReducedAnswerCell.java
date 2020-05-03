package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.List;

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

    public static ReducedAnswerCell prepareCell(Cell c, List<Player> playerList) {
        ReducedAnswerCell temp = new ReducedAnswerCell(c.getX(), c.getY());
        temp.setLevel(ReducedLevel.parseInt(c.getLevel().toInt()));

        if (!c.isFree()) {
            Worker w = ((Worker) ((Block) c).getPawn());
            for (Player p : playerList) {
                for (Worker worker : p.getWorkers()) {
                    if (w.equals(worker)) {
                        temp.setWorker(new ReducedWorker(w, p.nickName));
                        break;
                    }
                }
            }
        }

        return temp;
    }

}
