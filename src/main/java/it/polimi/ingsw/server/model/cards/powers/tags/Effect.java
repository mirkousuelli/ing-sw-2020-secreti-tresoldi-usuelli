package it.polimi.ingsw.server.model.cards.powers.tags;

/**
 * Enumeration that represents the type of effect that a card has
 * The effect can be an additional win condition (like for Chronus or Pan), a malus that is applied to the opponents
 * (like for Athena and Persephone), a special move or a special build
 */
public enum Effect {
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