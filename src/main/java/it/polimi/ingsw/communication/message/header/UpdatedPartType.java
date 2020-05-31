package it.polimi.ingsw.communication.message.header;

public enum UpdatedPartType {
    WORKER,
    PLAYER,
    GOD,
    CARD,
    BOARD;

    public static UpdatedPartType parseString(String str) {
        switch (str) {
            case "worker":
            case "placeWorkers":
            case "chooseWorker":
                return WORKER;

            case "god":
            case "chooseDeck":
            case "availableGods":
                return GOD;

            case "chooseCard":
                return CARD;

            case "chooseStarter":
                return PLAYER;

            case "board":
            case "move":
            case "build":
            case "askAdditionalPower":
            case "additionalPower":
                return BOARD;

            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case WORKER:
                return "worker";
            case GOD:
                return "god";
            case CARD:
                return "card";
            case PLAYER:
                return "player";
            case BOARD:
                return "board";
            default:
                return "";
        }
    }
}
