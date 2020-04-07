package it.polimi.ingsw.server.model.cards.powers.tags.malus;

public enum MalusLevel {
    UP, DOWN, SAME, DEFAULT;

    public static MalusLevel parseString(String str) {
        if (str.equalsIgnoreCase("UP"))
            return UP;
        else if (str.equalsIgnoreCase("DOWN"))
            return DOWN;
        else if (str.equalsIgnoreCase("SAME"))
            return SAME;
        else if (str.equalsIgnoreCase("DEFAULT"))
            return DEFAULT;
        else
            return null;
    }
}
