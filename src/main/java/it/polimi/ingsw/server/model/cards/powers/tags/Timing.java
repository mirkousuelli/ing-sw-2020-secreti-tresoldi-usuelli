package it.polimi.ingsw.server.model.cards.powers.tags;

/**
 * Enumeration for the timing of a power, representing when the power can be used
 * It can be at the beginning of the turn or at the end
 * For active powers such as move and build it can also be additional or default
 * For example Poseidon performs its power at the end of the turn.
 */
public enum Timing {
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
