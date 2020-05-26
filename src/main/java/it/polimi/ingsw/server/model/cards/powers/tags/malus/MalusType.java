package it.polimi.ingsw.server.model.cards.powers.tags.malus;

/**
 * Enumeration that represents the type of malus that is applied to the opponents
 * <p>
 * It can be a malus on the build or on the move
 */
public enum MalusType {
    BUILD,
    MOVE;

    public static MalusType parseString(String str) {
        if (str.equalsIgnoreCase("BUILD"))
            return BUILD;
        else if (str.equalsIgnoreCase("MOVE"))
            return MOVE;
        else
            return null;
    }
}
