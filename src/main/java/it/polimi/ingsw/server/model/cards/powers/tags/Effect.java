package it.polimi.ingsw.server.model.cards.powers.tags;

public enum Effect {
    //Card effect
    BUILD, MOVE, WIN_COND, MALUS;

    public static Effect parseString(String str) {
        if (str.equalsIgnoreCase("BUILD"))
           return BUILD;
        else if (str.equalsIgnoreCase("MOVE"))
            return MOVE;
        else if (str.equalsIgnoreCase("WIN"))
            return WIN_COND;
        else if (str.equalsIgnoreCase("MALUS"))
            return MALUS;
        else
            return null;
    }
}
