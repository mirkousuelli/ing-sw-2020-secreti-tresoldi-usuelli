package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.cards.gods.God;

public class ReducedPlayer {

    private String nickname;
    private String color;
    private God god;

    public ReducedPlayer(String player, String color, God god) {
        this.nickname = player;
        this.color = color;
        this.god = god;
    }

    public ReducedPlayer(String player, String color) {
        this.nickname = player;
        this.color = color;
    }

    public ReducedPlayer() {}

    public ReducedPlayer(String player, God god) {
        this(player, null, god);
    }

    public ReducedPlayer(String player) {
        this(player, null, null);
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

    public God getGod() {
        return god;
    }

    public void setGod(God god) {
        this.god = god;
    }
}

