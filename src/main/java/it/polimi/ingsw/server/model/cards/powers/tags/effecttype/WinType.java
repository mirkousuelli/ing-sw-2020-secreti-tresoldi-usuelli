package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

/**
 * Enumeration that represents the type of win condition
 * It can be a move down of two or more levels (for Pan), the presence of five or more completed towers on the board
 * (for Chronus) or just the regular win condition for everyone else
 */
public enum WinType {
    DOWN_FROM_TWO, FIVE_TOWER, DEFAULT;

    public static WinType parseString(String str) {
        if (str.equalsIgnoreCase("DOWNFROMTWO"))
            return DOWN_FROM_TWO;
        else if (str.equalsIgnoreCase("FIVETOWER"))
            return FIVE_TOWER;
        else if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else
            return null;
    }
}
