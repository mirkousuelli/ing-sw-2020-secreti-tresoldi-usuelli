package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Cell;

public class Persephone extends Card {

    public Persephone() {
        /*
         *
         * */
        super(God.PERSEPHONE, Effect.OPPONENT_TURN);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*
         *
         * */
        return true;
    }
}