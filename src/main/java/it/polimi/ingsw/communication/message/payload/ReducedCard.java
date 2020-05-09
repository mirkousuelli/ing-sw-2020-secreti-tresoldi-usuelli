package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.gods.God;

public class ReducedCard {

    God god;
    String description;

    public ReducedCard() {}

    public ReducedCard(God god, String description) {
        this.god = god;
        this.description = description;
    }

    public ReducedCard(Card card) {
        this.god = card.getGod();
        this.description = card.getDescription();
    }

    public God getGod() {
        return god;
    }

    public void setGod(God god) {
        this.god = god;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
