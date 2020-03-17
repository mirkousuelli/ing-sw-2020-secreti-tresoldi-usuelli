package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.map.Cell;

public abstract class Card {
    public final God god;
    public final Effect effect;

    public Card(God god, Effect effect) {
        this.god = god;
        this.effect = effect;
    }

    public abstract boolean usePower(Cell cell);
}