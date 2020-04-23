package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

public enum WinType {
    DOWNTOFROMTWO, FIVETOWER, DEFAULT;

    public static WinType parseString(String str) {
        if (str.equalsIgnoreCase("DOWNFROMTWO"))
            return DOWNTOFROMTWO;
        else if (str.equalsIgnoreCase("FIVETOWER"))
            return FIVETOWER;
        else if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else
            return null;
    }
}
