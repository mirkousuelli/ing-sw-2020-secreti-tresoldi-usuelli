package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

public enum BlockType {
    // Cosa posso costruire
    DOME, // Posso costruire solo DOME
    NOT_DOME, // Posso costruire tutto tranne DOME
    DEFAULT; // Posso costruire tutto

    public static BlockType parseString(String str) {
        if (str.equalsIgnoreCase("DOME"))
            return DOME;
        else if (str.equalsIgnoreCase("NOTDOME"))
            return NOT_DOME;
        else if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else
            return null;
    }
}
