package it.polimi.ingsw.server.model.cards.powers.tags.malus;

/**
 * Enumeration that represents the type of malus that is applied to the opponents
 * <p>
 * It can be a malus on the build or on the move
 */
public enum MalusType {
    BUILD,
    MOVE,
    WIN_COND;

    public static MalusType parseString(String str) {
        if (str.equalsIgnoreCase("BUILD"))
            return BUILD;
        else if (str.equalsIgnoreCase("MOVE"))
            return MOVE;
        else if (str.equalsIgnoreCase("WINCOND"))
            return WIN_COND;
        else
            return null;
    }
}
