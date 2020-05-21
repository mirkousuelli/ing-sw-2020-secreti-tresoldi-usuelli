package it.polimi.ingsw.server.model.cards.powers.tags;

/**
 * Enumeration that represents the initial position of the worker to be able to use the power
 * It can be any level from ground to top
 * For example Poseidon requires a worker on the ground to perform its power
 */
public enum WorkerPosition {
    GROUND, BOTTOM, MIDDLE, TOP, DEFAULT;

    public static WorkerPosition parseString(String str) {
        if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else if (str.equalsIgnoreCase("GROUND"))
            return GROUND;
        else if (str.equalsIgnoreCase("BOTTOM"))
            return BOTTOM;
        else if (str.equalsIgnoreCase("MIDDLE"))
            return MIDDLE;
        else if (str.equalsIgnoreCase("TOP"))
            return TOP;
        else
            return null;
    }
}
