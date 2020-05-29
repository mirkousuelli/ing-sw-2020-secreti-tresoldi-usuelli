package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.Card;
import it.polimi.ingsw.server.model.cards.gods.God;
import it.polimi.ingsw.server.model.cards.powers.tags.Effect;
import it.polimi.ingsw.server.model.cards.powers.tags.Timing;
import it.polimi.ingsw.server.model.cards.powers.tags.effecttype.BlockType;

public class ReducedCard {

    private God god;
    private String description;
    private Effect effect;
    private boolean isDomePower;
    private boolean additionalPower;

    public ReducedCard() {}

    public ReducedCard(Card card) {
        this.god = card.getGod();
        this.description = card.getDescription();
        effect = card.getPower(0).getEffect();
        additionalPower = card.getPower(0).getTiming().equals(Timing.ADDITIONAL);
        isDomePower = effect.equals(Effect.BUILD) && card.getPower(0).getAllowedAction().equals(BlockType.DOME);
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

    public boolean isDomePower() {
        return isDomePower;
    }

    public void setDomePower(boolean isDomePower) {
        this.isDomePower = isDomePower;
    }
}
