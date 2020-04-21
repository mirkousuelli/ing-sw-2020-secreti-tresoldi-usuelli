/*
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.cards.powers;

import it.polimi.ingsw.server.model.cards.powers.tags.*;
import it.polimi.ingsw.server.model.cards.powers.tags.WorkerType;

/**
 * Represents a generic power of a god.
 * <p>
 *     It allows us to describe each characteristic of every god.
 *     Different attributes are used to describe a god's power.
 *     For example, Demeter's power allows to build one additional time, so {@code effect} will be {@code Effect.BUILD} and {@code timing} will be {@code Timing.ADDITIONAL}.
 * <p>
 *     The values of the attributes for each god are loaded from a xml file by a xml parser.
 *     So, it is possible to add new gods with only little changes.
 * <p>
 *     This class is immutable, so the xml parser uses a temporary class that extends this class.
 *     The temporary sub-class only has a setter for each attribute of Power.
 *     Powers must have also a constructor which take an instance of the temporary sub-class to initialize by copy its attributes.
 *     Power should have a getter for each attribute of Power.
 */
public class Power<S> {
    /**
     * Represents the state the worker must be to be able to use the power.
     * It can be unmoved or default.
     * For example, Poseidon to perform its power requires an unmoved worker.
     */
    protected WorkerType workerType;
    /**
     * Represents the position the worker must be to be able to use the power.
     * It can be any level from ground to top.
     * For example, Poseidon to perform its power requires a worker on the ground.
     */
    protected WorkerPosition workerInitPos;
    /**
     * Represents the effect of the power.
     * It can be a move, a build, a malus or an extra win condition.
     * For example, Poseidon to perform its power requires an unmoved worker.
     */
    protected Effect effect;
    /**
     * Indicates when the power can be used.
     * It can be at the start of the turn or at the end.
     * For active powers such as move and build can also be additional or default.
     * For example, Poseidon performs its power at the end of the turn.
     */
    protected Timing timing;
    /**
     * Symbolizes restrictions on the power itself.
     * In other words, it contains constraints about where is it possible to build or move with a god's power.
     * For example, Demeter can build one additional time but not on the same space.
     */
    protected Constraints constraints;
    /**
     * */
    protected S allowedAction;
    /**
     * Represents a complementary malus.
     * That is to say, a malus applied on the owner of the card when he will use its god's power.
     * It could be a move malus or a build one.
     * Also, it can be permanent or limited to a specific number of turns.
     * For example, Prometheus's power allows to build before and after moving only if the player does not move up.
     */
    protected Malus personalMalus;

    /**
     * Constructor.
     */
    public Power() {
        constraints = new Constraints();
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

    public S getAllowedAction() {
        return allowedAction;
    }

    public void setAllowedAction(S allowedAction) {
        this.allowedAction = allowedAction;
    }

    public Malus getPersonalMalus() {
        return personalMalus;
    }

    public void setPersonalMalus(Malus malus) {
        this.personalMalus = malus;
    }

    /**
     * Implements the algorithm to do the actions related to a god's power.
     * <p>
     *     It analyses the values of the attributes of class Power and performs the right actions for the detected power.
     *     Each one of these subclasses represents a different type of power.
     *     That is to say, powers can be active or passive.
     *     <ol>
     *         <li>Active powers must be explicitly called to be used. They can be move powers or build ones.
     *         <li>Passive powers are always active or activated on a particular condition, they cannot be called by the player. They can be malus powers or win condition ones.
     *     </ol>
     *
     * @return the outcome of the operation.
     *
     * <h4>Implementation notes</h4>
     * This method must be overloaded by each subclass of Power.
     */
    public boolean usePower() {
        return false;
    }
}
