package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.Card;

public class ReducedPlayer {

    private String nickname;
    private String color;
    private ReducedCard card;
    private boolean isCreator;

    public ReducedPlayer() {}

    public ReducedPlayer(String player, String color, Card card, boolean isCreator) {
        this.nickname = player;
        this.color = color;
        this.isCreator = isCreator;

        if (card == null)
            this.card = null;
        else
            this.card = new ReducedCard(card);
    }

    public ReducedPlayer(String player, String color) {
        this(player, color, null, false);
    }

    public ReducedPlayer(String player, Card card) {
        this(player, null, card, false);
    }

    public ReducedPlayer(String player) {
        this(player, null, null, false);
    }

    public ReducedPlayer(String player, String color ,boolean isCreator) {
        this(player, color, null, isCreator);
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ReducedCard getCard() {
        return card;
    }

    public void setCard(ReducedCard card) {
        this.card = card;
    }

    public boolean isCreator() {
        return isCreator;
    }

    public void setCreator(boolean creator) {
        isCreator = creator;
    }
}

