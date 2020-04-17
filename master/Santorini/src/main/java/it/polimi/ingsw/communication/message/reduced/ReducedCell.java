package it.polimi.ingsw.communication.message.reduced;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

public class ReducedCell {
    private final int x;
    private final int y;
    private final ReducedLevel level;
    private final ReducedAction action;
    private final ReducedWorker worker;

    public ReducedCell(int x, int y, Level level, DemandType action, Worker worker, Player player) {
        this.x = x;
        this.y = y;
        this.level = ReducedLevel.parseInt(level.toInt());
        this.action = ReducedAction.parseString(action.toString());
        this.worker = new ReducedWorker(worker, player.nickName);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ReducedLevel getLevel() {
        return level;
    }

    public ReducedAction getAction() {
        return action;
    }

    public ReducedWorker getWorker() {
        return worker;
    }

    /*public void setLevel(ReducedLevel level) {
        this.level = level;
    }

    public void setAction(ReducedAction action) {
        this.action = action;
    }

    public void setWorker(ReducedWorker worker) {
        this.worker = worker;
    }*/
}
