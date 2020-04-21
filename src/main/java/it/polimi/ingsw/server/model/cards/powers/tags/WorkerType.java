package it.polimi.ingsw.server.model.cards.powers.tags;

public enum WorkerType {
    //Which worker the power uses
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
