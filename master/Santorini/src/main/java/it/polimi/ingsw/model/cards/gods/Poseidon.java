package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Cell;

public class Poseidon extends Card {

    public Poseidon() {
        /*
         *
         * */
        super(God.POSEIDON, Effect.YOUR_TURN);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*
         *
         * */
        return true;
    }
}