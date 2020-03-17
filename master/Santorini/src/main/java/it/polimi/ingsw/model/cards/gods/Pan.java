package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Cell;

public class Pan extends Card {

    public Pan() {
        /*
         *
         * */
        super(God.PAN, Effect.WIN_CONDITION);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*
         *
         * */
        return true;
    }
}