package it.polimi.ingsw.model.cards.powers.tags;

import javax.swing.*;

public enum Effect {
    //Effetto della carta
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
