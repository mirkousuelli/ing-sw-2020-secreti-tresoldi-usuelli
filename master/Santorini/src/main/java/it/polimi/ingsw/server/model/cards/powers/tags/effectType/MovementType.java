package it.polimi.ingsw.server.model.cards.powers.tags.effectType;

public enum MovementType {
    //Se il potere ha un effetto MOVE si specifica che tipo di move
    //(i.e. APOLLO ha SWAP, MINOTAUR ha PUSH, altri DEFAULT)
    SWAP, PUSH, DEFAULT;

    public static MovementType parseString(String str) {
        if (str.equalsIgnoreCase("SWAP"))
            return SWAP;
        else if (str.equalsIgnoreCase("PUSH"))
            return PUSH;
        else if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else
            return null;
    }
}
