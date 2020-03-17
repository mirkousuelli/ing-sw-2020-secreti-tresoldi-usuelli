package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Cell;

public class Apollo extends Card {

    public Apollo() {
        /*
         *
         * */
        super(God.APOLLO, Effect.MOVE);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*
         *
         * */
        return true;
    }
}