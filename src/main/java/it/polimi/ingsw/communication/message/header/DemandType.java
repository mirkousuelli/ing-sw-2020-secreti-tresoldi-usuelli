package it.polimi.ingsw.communication.message.header;

import java.util.Arrays;

/**
 * Enumeration that contains the type of demand that the player sends and represents the type of action that the
 * player wants to make
 * <p>
 * It is used as header of the message sent by the player
 */
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
    ASK_ADDITIONAL_POWER,
    ADDITIONAL_POWER,
    USE_POWER,
    NEW_GAME;

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
            case "additionalPower":
                return ADDITIONAL_POWER;
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
            case ADDITIONAL_POWER:
                return "additionalPower";
            case NEW_GAME:
                return "newGame";
            default:
                return "";
        }
    }

    /**
     * Method that returns the following state, depending on the current one
     *
     * @param currentState the current state of the game
     * @param isCreator parameter that tells if the player is the creator of the lobby
     * @return the next state based on the current one
     */
    public static DemandType getNextState(DemandType currentState, boolean isCreator) {
        if (currentState.equals(CONNECT) && (!isCreator))
            return START;

        if (currentState.equals(START) && !isCreator)
            return CHOOSE_CARD;

        if (currentState.equals(CHOOSE_CARD) && !isCreator)
                return PLACE_WORKERS;

        if (currentState.equals(ADDITIONAL_POWER) || currentState.equals(BUILD))
            return CHOOSE_WORKER;

        //repeat
        if (currentState.equals(NEW_GAME))
            return START;

        //pick the next one in numerical order
        return Arrays.stream(DemandType.values())
                .filter(state -> state.ordinal() == currentState.ordinal() + 1)
                .reduce(null, (a, b) -> a != null
                        ? a
                        : b
                );
    }
}
