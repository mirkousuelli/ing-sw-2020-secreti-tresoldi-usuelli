package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

public enum WinType {
    DOWN_TO_FROM_TWO, FIVE_TOWER, DEFAULT;

    public static WinType parseString(String str) {
        if (str.equalsIgnoreCase("DOWNFROMTWO"))
            return DOWN_TO_FROM_TWO;
        else if (str.equalsIgnoreCase("FIVETOWER"))
            return FIVE_TOWER;
        else if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else
            return null;
    }
}
