package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Block;
import it.polimi.ingsw.server.model.map.Cell;
import it.polimi.ingsw.server.model.map.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents the reduced version of a cell that the player receives as payload in a message. It extends
 * {@link ReducedDemandCell} by adding new attributes and methods.
 * <p>
 * It contains the coordinates of this cell and the gender of the worker on the cell (if the cell is occupied by a
 * worker), adding information like its level, the list of possible actions and the worker that can be on this cell
 */
public class ReducedAnswerCell extends ReducedDemandCell {

    private ReducedLevel level;
    private ReducedLevel prevLevel;
    private List<ReducedAction> actionList;
    private ReducedWorker worker;

    public ReducedAnswerCell() {}

    public ReducedAnswerCell(int x, int y) {
        this(x, y , null);
    }

    /**
     * Constructor of the reduced answer cell, which recalls its super class
     * <p>
     * It initialises its attributes, setting its coordinates to the designated one and all the other attributes to
     * the classic behaviour (for example setting its level to ground and the list of action to default)
     *
     * @param x x-coordinate of the cell
     * @param y y-coordinate of the cell
     * @param worker the worker that can be on this cell
     */
    public ReducedAnswerCell(int x, int y, ReducedWorker worker) {
        super(x, y);
        level = ReducedLevel.GROUND;
        prevLevel = ReducedLevel.GROUND;
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

    /**
     * Method that replaces the default action by creating a list of action and adding the new action
     *
     * @param action the action that is added to the list of actions
     */
    public void replaceDefaultAction(ReducedAction action) {
        actionList = new ArrayList<>();
        actionList.add(action);
    }

    /**
     * Method that resets the list of actions to default
     */
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

    public ReducedLevel getPrevLevel() {
        return prevLevel;
    }

    public void setPrevLevel(ReducedLevel prevLevel) {
        this.prevLevel = prevLevel;
    }

    /**
     * Method that prepares the cell passed as parameter, returning its reduced version after setting its coordinates
     * and its level. If the chosen cell is occupied by a worker it also initialises the worker (and his id) on the cell
     *
     * @param c the cell that the reduced version is obtained from
     * @param playerList list of players
     * @return the reduced version of the cell (with the eventual worker on it)
     */
    public static ReducedAnswerCell prepareCell(Cell c, List<Player> playerList) {
        ReducedAnswerCell temp = new ReducedAnswerCell(c.getX(), c.getY());
        temp.setLevel(ReducedLevel.parseInt(c.getLevel().toInt()));
        temp.setPrevLevel(ReducedLevel.parseInt(((Block) c).getPreviousLevel().toInt()));

        if (!c.isFree()) {
            Worker w = ((Worker) ((Block) c).getPawn());
            for (Player p : playerList) {
                for (Worker worker : p.getWorkers()) {
                    if (w.equals(worker)) {
                        temp.setWorker(new ReducedWorker(w, p.nickName));
                        temp.getWorker().setId(w.getId());
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
