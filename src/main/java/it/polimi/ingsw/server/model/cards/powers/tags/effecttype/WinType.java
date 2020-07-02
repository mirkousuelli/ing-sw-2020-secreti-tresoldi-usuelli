package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

/**
 * Enumeration that represents the type of win condition
 * <p>
 * It can be a move down of two or more levels (for Pan) or the presence of five or more completed towers on the board
 * (for Chronus)
 */
public enum WinType {
    DOWN_FROM_TWO, FIVE_TOWER;

    public static WinType parseString(String str) {
        if (str.equalsIgnoreCase("DOWNFROMTWO"))
            return DOWN_FROM_TWO;
        else if (str.equalsIgnoreCase("FIVETOWER"))
            return FIVE_TOWER;
        else
            return null;
    }
}
