package it.polimi.ingsw.server.model.cards.powers.tags.effecttype;

/**
 * Enumeration that represents the type of block that can be built by a God power
 * <p>
 * It can be a dome (like for Atlas), anything but a dome (like for Hephaestus) or everything
 */
public enum BlockType {
    DOME,
    NOT_DOME,
    DEFAULT;

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
