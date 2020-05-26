package it.polimi.ingsw.server.model.cards.powers.tags;

/**
 * Enumeration that represents the state the {@link it.polimi.ingsw.server.model.map.Worker} must be to be able to use the power
 * <p>
 * It can be unmoved or default
 * <p>
 * For example Poseidon requires an unmoved worker in order to perform its power, while for every other God
 * it doesn't matter
 */
public enum WorkerType {
    UNMOVED_WORKER, DEFAULT;

    public static WorkerType parseString(String str) {
        if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else if (str.equalsIgnoreCase("UNMOVED"))
            return UNMOVED_WORKER;
        else
            return null;
    }
}
