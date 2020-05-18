package it.polimi.ingsw.communication.message.header;

public enum DemandType {
    CONNECT,
    RELOAD,
    CREATE_GAME,
    START,
    CHOOSE_DECK,
    AVAILABLE_GODS,
    CHOOSE_CARD,
    CHOOSE_STARTER,
    PLACE_WORKERS,
    CHOOSE_WORKER,
    MOVE,
    BUILD,
    USE_POWER,
    ASK_ADDITIONAL_POWER,
    CHANGE_TURN,
    VICTORY,
    DEFEAT,
    NEW_GAME;

    public static DemandType parseString(String str) {
        switch (str) {
            case "connect":
                return CONNECT;
            case "reload":
                return RELOAD;
            case "createGame":
                return CREATE_GAME;
            case "start":
                return START;
            case "chooseDeck":
                return CHOOSE_DECK;
            case "availableGods":
                return AVAILABLE_GODS;
            case "chooseCard":
                return CHOOSE_CARD;
            case "chooseStarter":
                return CHOOSE_STARTER;
            case "placeWorkers":
                return PLACE_WORKERS;
            case "chooseWorker":
                return CHOOSE_WORKER;
            case "move":
                return MOVE;
            case "build":
                return BUILD;
            case "usePower":
                return USE_POWER;
            case "askAdditionalPower":
                return ASK_ADDITIONAL_POWER;
            case "changeTurn":
                return CHANGE_TURN;
            case "victory":
                return VICTORY;
            case "defeat":
                return DEFEAT;
            case "newGame":
                return NEW_GAME;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case CONNECT:
                return "connect";
            case RELOAD:
                return "reload";
            case CREATE_GAME:
                return "createGame";
            case START:
                return "start";
            case CHOOSE_DECK:
                return "chooseDeck";
            case AVAILABLE_GODS:
                return "availableGods";
            case CHOOSE_CARD:
                return "chooseCard";
            case CHOOSE_STARTER:
                return "chooseStarter";
            case PLACE_WORKERS:
                return "placeWorkers";
            case CHOOSE_WORKER:
                return "chooseWorker";
            case MOVE:
                return "move";
            case BUILD:
                return "build";
            case USE_POWER:
                return "usePower";
            case ASK_ADDITIONAL_POWER:
                return "askAdditionalPower";
            case CHANGE_TURN:
                return "changeTurn";
            case VICTORY:
                return "victory";
            case DEFEAT:
                return "defeat";
            case NEW_GAME:
                return "newGame";
            default:
                return "";
        }
    }
}
