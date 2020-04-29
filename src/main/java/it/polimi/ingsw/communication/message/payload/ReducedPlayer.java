package it.polimi.ingsw.communication.message.payload;

public class ReducedPlayer {

    private String nickname;
    private String color;

    public ReducedPlayer(String player, String color) {
        this.nickname = player;
        this.color = color;
    }

    public ReducedPlayer() {}

    public ReducedPlayer(String player) {
        this(player, null);
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
}

