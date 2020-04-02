package it.polimi.ingsw.model.cards.powers.tags;

public enum WorkerPosition {
    //Posizione iniziale che il worker usato dal potere deve avere
    //(i.e. Poseidon una worker che si trova al livello GROUND)
    //DEFAULT significa che va bene qualsiasi livello
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
