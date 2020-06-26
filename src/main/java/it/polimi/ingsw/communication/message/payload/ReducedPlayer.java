package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.Card;

/**
 * Class that represents the reduced version of a player, which contains every piece of information that is necessary
 * <p>
 * It contains its name, its color, the card he owns (in the reduced version) and information about him being the
 * creator of the lobby or not
 */
public class ReducedPlayer {
    private String nickname;
    private String color;
    private ReducedCard card;
    private boolean isCreator;

    /**
     * Constructor of the reduced player, initialising it with the information passed with the parameters
     *
     * @param player    the name of the player, which is unique in a game (two or more players cannot have the same nickname)
     * @param color     the color of the player
     * @param card      the card that the player has: it is set to its reduced version or to null if he doesn't have it
     * @param isCreator parameter that tells if the player is the one who created the lobby
     */
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

    public ReducedPlayer(String player, String color, boolean isCreator) {
        this(player, color, null, isCreator);
    }

    public ReducedPlayer(String player, boolean isCreator) {
        this(player, null, null, isCreator);
    }

    public ReducedPlayer() {

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

