package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

public class JGod {
    private JCard card;
    private JMini mini;

    public JGod(God god) {
        card = new JCard(god);
        mini = new JMini(god);
    }

    public JCard getCard() {
        return this.card;
    }

    public JMini getMini() {
        return this.mini;
    }

    public God getGod() {
        return this.mini.getGod();
    }
}
