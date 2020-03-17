package it.polimi.ingsw.model.cards.gods;

import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.Effect;
import it.polimi.ingsw.model.cards.God;
import it.polimi.ingsw.model.map.Cell;

public class Hephaestus extends Card {

    public Hephaestus() {
        /*
         *
         * */
        super(God.HEPHAESTUS, Effect.BUILD);
    }

    @Override
    public boolean usePower(Cell cell) {
        /*
         *
         * */
        return true;
    }
}