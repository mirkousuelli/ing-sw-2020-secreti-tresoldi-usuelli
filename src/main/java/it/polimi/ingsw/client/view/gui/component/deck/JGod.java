package it.polimi.ingsw.client.view.gui.component.deck;

import it.polimi.ingsw.server.model.cards.gods.God;

public class JGod {
    private JCard card;
    private JMini mini;
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
        return this.card;
    }

    public JMini getMini() {
        return this.mini;
    }

    public God getGod() {
        return this.mini.getGod();
    }

    public void setDescription(String description) {
        this.description = description;
        //this.card.setToolTipText(description);
    }

    public String getDescription() {
        return this.description;
    }
}
