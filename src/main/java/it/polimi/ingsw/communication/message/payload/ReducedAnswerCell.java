package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

public class ReducedAnswerCell extends ReducedDemandCell {

    private ReducedLevel level;
    private List<ReducedAction> actionList;
    private ReducedWorker worker;

    public ReducedAnswerCell() {}

    public ReducedAnswerCell(int x, int y) {
        this(x, y , null);
    }

    public ReducedAnswerCell(int x, int y, ReducedWorker worker) {
        super(x, y);
        level = ReducedLevel.GROUND;
        actionList = new ArrayList<>();
        actionList.add(ReducedAction.DEFAULT);
        this.worker = worker;
    }

    public List<ReducedAction> getActionList() {
        return actionList;
    }

    public void setActionList(List<ReducedAction> actionList) {
        this.actionList = actionList;
    }

    public void replaceDefaultAction(ReducedAction action) {
        actionList = new ArrayList<>();
        actionList.add(action);
    }

    public void resetAction() {
        replaceDefaultAction(ReducedAction.DEFAULT);
    }

    public ReducedLevel getLevel() {
        return level;
    }

    public ReducedAction getAction(int i) {
        return actionList.get(i);
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
        actionList.add(action);
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

    public static List<ReducedAnswerCell> prepareList(ReducedAction reducedAction, List<Player> playerList, List<Cell> possibleAction, List<Cell> specialAction) {
        List<ReducedAnswerCell> toReturn = new ArrayList<>();
        ReducedAnswerCell temp;

        for (Cell c : possibleAction) {
            temp = ReducedAnswerCell.prepareCell(c, playerList);
            temp.replaceDefaultAction(reducedAction);
            toReturn.add(temp);
        }

        ReducedAnswerCell found;
        for (Cell c : specialAction) {
            found = null;
            for (ReducedAnswerCell reducedCell : toReturn) {
                if(c.getX() == reducedCell.getX() && c.getY() == reducedCell.getY()) {
                    found = reducedCell;
                    break;
                }
            }
            if (found == null) {
                temp = ReducedAnswerCell.prepareCell(c, playerList);
                temp.replaceDefaultAction(ReducedAction.USEPOWER);
                toReturn.add(temp);
            }
            else if (!found.getActionList().contains(ReducedAction.DEFAULT))
                found.getActionList().add(ReducedAction.USEPOWER);
        }

        return toReturn;
    }

}
