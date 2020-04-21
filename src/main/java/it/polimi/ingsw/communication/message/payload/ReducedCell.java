package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.map.Level;
import it.polimi.ingsw.server.model.map.Worker;

public class ReducedCell extends MessageCell {

    private /*final*/ ReducedLevel level;
    private /*final*/ ReducedAction action;
    private /*final*/ ReducedWorker worker;

    public ReducedCell(int x, int y, Level level, DemandType action, Worker worker, Player player) {
        super(x, y);
        this.level = ReducedLevel.parseInt(level.toInt());
        this.action = ReducedAction.parseString(action.toString());
        this.worker = new ReducedWorker(worker, player.nickName);
    }

    public ReducedCell(int x, int y) {
        super(x, y);
        level = null;
        action = null;
        worker = null;
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
