package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

/**
 * Enumeration that represents the type of movement that a power can allow
 * It can be a swap (for Apollo), a push (for Minotaur) or a normal move
 */
public enum MovementType {
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
