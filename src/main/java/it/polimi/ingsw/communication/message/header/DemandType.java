package it.polimi.ingsw.communication.message.header;

import java.util.Arrays;

public enum DemandType {
    CONNECT,
    CREATE_GAME,
    START,
    CHOOSE_DECK,
    CHOOSE_CARD,
    CHOOSE_STARTER,
    PLACE_WORKERS,
    CHOOSE_WORKER,
    MOVE,
    BUILD,
    USE_POWER,
    ASK_ADDITIONAL_POWER;

    public static DemandType parseString(String str) {
        switch (str) {
            case "connect":
                return CONNECT;
            case "createGame":
                return CREATE_GAME;
            case "start":
                return START;
            case "chooseDeck":
                return CHOOSE_DECK;
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
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case CONNECT:
                return "connect";
            case CREATE_GAME:
                return "createGame";
            case START:
                return "start";
            case CHOOSE_DECK:
                return "chooseDeck";
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
            default:
                return "";
        }
    }

    public static DemandType getNextState(DemandType currentState, boolean isCreator) {
        if (currentState.equals(CONNECT)) {
            if (isCreator)
                return CREATE_GAME;
            else
                return START;
        }

        if (currentState.equals(CREATE_GAME))
            return START;

        if (currentState.equals(START)) {
            if (!isCreator)
                return CHOOSE_CARD;
        }

        if (currentState.equals(CHOOSE_CARD)) {
            if (isCreator)
                return CHOOSE_STARTER;
            else
                return PLACE_WORKERS;
        }

        //pick the next one in numerical order
        return Arrays.stream(DemandType.values())
                .filter(state -> state.ordinal() == currentState.ordinal() + 1)
                .reduce(null, (a, b) -> a != null
                        ? a
                        : b
                );
    }
}
