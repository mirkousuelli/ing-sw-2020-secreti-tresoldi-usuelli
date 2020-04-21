package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.Player;

public class ReducedPlayer {

    private final String nickname;
    private final String color;

    public ReducedPlayer(Player player, String color) {
        this.nickname = player.nickName;
        this.color = color;
    }

    public String getNickname() {
        return nickname;
    }

    public String getColor() {
        return color;
    }
}
