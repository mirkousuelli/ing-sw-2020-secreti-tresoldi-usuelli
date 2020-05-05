package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

public enum BlockType {
    // What I can build
    DOME, // that means I can build only a dome
    NOT_DOME, // that means I can build anything but a dome
    DEFAULT; // that means I can build anything

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
