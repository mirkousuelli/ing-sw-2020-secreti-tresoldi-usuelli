package it.polimi.ingsw.server.model.cards.powers.tags.malus;

public enum MalusType {
    BUILD,
    MOVE;

    public static MalusType parseString(String str) {
        if (str.equalsIgnoreCase("BUILD"))
            return BUILD;
        else if (str.equalsIgnoreCase("MOVE"))
            return MOVE;
        else
            return null;
    }
}
