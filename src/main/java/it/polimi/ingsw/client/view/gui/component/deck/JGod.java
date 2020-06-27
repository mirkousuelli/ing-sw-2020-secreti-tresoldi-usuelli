package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

public class JGod {

    private final JCard card;
    private final JMini mini;

    private String description;

    public JGod(God god) {
        card = new JCard(god);
        mini = new JMini(god);
    }

    public JGod(God god, String description) {
        card = new JCard(god);
        mini = new JMini(god);
        this.description = description;
    }

    public JCard getCard() {
        return card;
    }

    public JMini getMini() {
        return mini;
    }

    public God getGod() {
        return mini.getGod();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
