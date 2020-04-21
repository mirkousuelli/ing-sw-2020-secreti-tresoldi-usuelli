package it.polimi.ingsw.server.model.cards.powers.tags.malus;

public enum MalusType {
    BUILD, // Malus sulla build
    MOVE; // Malus sulla move

    public static MalusType parseString(String str) {
        if (str.equalsIgnoreCase("BUILD"))
            return BUILD;
        else if (str.equalsIgnoreCase("MOVE"))
            return MOVE;
        else
            return null;
    }
}
