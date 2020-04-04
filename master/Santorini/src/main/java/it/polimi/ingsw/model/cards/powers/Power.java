/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.model.cards.powers;

import it.polimi.ingsw.model.cards.powers.tags.*;
import it.polimi.ingsw.model.cards.powers.tags.effectType.BlockType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.MovementType;
import it.polimi.ingsw.model.cards.powers.tags.effectType.WinType;
import it.polimi.ingsw.model.cards.powers.tags.WorkerType;
import it.polimi.ingsw.model.map.Worker;

public class Power {
    protected WorkerType workerType;
    protected WorkerPosition workerInitPos;
    protected Effect effect;
    protected Timing timing;
    protected Constraints constraints;
    protected BlockType allowedBlock;
    protected MovementType allowedMove;
    protected WinType allowedWin;
    protected Malus malus;
    protected Malus personalMalus;

    protected int numberOfActionsRemaining;
    protected Worker workerToUse;


    public Power() {
        constraints = new Constraints();
        malus = new Malus();
        personalMalus = new Malus();
    }

    public WorkerType getWorkerType() {
        return workerType;
    }

    public void setWorkerType(WorkerType workerType) {
        this.workerType = workerType;
    }

    public WorkerPosition getWorkerInitPos() {
        return workerInitPos;
    }

    public void setWorkerInitPos(WorkerPosition workerInitPos) {
        this.workerInitPos = workerInitPos;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public Timing getTiming() {
        return timing;
    }

    public void setTiming(Timing timing) {
        this.timing = timing;
    }

    public Constraints getConstraints() {
        return constraints;
    }

    public void setConstraints(Constraints constraints) {
        this.constraints = constraints;
    }

    public BlockType getAllowedBlock() {
        return allowedBlock;
    }

    public void setAllowedBlock(BlockType allowedBlock) {
        this.allowedBlock = allowedBlock;
    }

    public MovementType getAllowedMove() {
        return allowedMove;
    }

    public void setAllowedMove(MovementType allowedMove) {
        this.allowedMove = allowedMove;
    }

    public WinType getAllowedWin() {
        return allowedWin;
    }

    public void setAllowedWin(WinType allowedWin) {
        this.allowedWin = allowedWin;
    }

    public Malus getMalus() {
        return malus;
    }

    public Malus getPersonalMalus() {
        return personalMalus;
    }

    public void setNumberOfActionsRemaining() {
        numberOfActionsRemaining = constraints.getNumberOfAdditional();
    }

    public boolean usePower() {
        return false;
    }
}
