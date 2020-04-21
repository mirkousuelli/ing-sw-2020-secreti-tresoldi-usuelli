package it.polimi.ingsw.server.model.cards.powers.tags;

public enum Timing {
    //When the power can be used
    ADDITIONAL,
    START_TURN,
    END_TURN,
    DEFAULT;

    public static Timing parseString(String str) {
        if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else if (str.equalsIgnoreCase("ADDITIONAL"))
            return ADDITIONAL;
        else if (str.equalsIgnoreCase("STARTTURN"))
            return START_TURN;
        else if (str.equalsIgnoreCase("ENDTURN"))
            return END_TURN;
        else
            return null;
    }
}
