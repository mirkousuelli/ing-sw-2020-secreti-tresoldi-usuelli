package it.polimi.ingsw.server.model.cards.powers.tags;

public enum WorkerPosition {
    //Initial position a worker must have to activate the power
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
