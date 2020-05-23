package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;

public class ReducedCard {

    private God god;
    private String description;
    private Effect effect;
    private boolean additionalPower;

    public ReducedCard() {}

    public ReducedCard(Card card) {
        this.god = card.getGod();
        this.description = card.getDescription();
        effect = card.getPower(0).getEffect();
        additionalPower = card.getPower(0).getTiming().equals(Timing.ADDITIONAL);
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

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public boolean isAdditionalPower() {
        return additionalPower;
    }

    public void setAdditionalPower(boolean additionalPower) {
        this.additionalPower = additionalPower;
    }
}
