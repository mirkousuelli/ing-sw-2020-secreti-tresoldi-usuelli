package it.polimi.ingsw.server.model.cards.powers.tags;

public enum WorkerType {
    //Lavoratore viene usato per il potere
    //(i.e. Poseidon usa UNMOVED_WORKER)
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
