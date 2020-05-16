package it.polimi.ingsw.client.view.gui.button.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

public class JGod {
    private JCard card;
    private JMini mini;

    public JGod(God god) {
        card = new JCard(god);
        mini = new JMini(god);
    }
}
