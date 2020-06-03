package it.polimi.ingsw.communication.message;

import it.polimi.ingsw.communication.message.header.DemandType;
import it.polimi.ingsw.communication.message.payload.ReducedMessage;

/**
 * Class that represents the demand that the player sends
 * <p>
 * For example if the player decides to make a move, the demand contains a {@link DemandType} that is {@code MOVE}
 * a payload that is {@code ReducedDemandCell}
 * <p>
 * It extends {@link Message}
 *
 * @param <S> the payload containing the object to modify
 */
public class Demand<S> extends Message<DemandType, S> {

    public Demand(DemandType header, S payload) {
        super(header, payload);
    }

    public Demand(DemandType header) {
        this(header, (S) new ReducedMessage("null"));
    }

    public Demand(Demand<S> msg) {
        super(msg.getHeader(), msg.getPayload());
    }

    public Demand(){}

}
