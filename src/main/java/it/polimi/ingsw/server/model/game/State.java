package it.polimi.ingsw.server.model.game;

/**
 * Enumeration that contains all possible states of the game
 */
public enum State {
    START,
    CHOOSE_CARD,
    CHOOSE_STARTER,
    PLACE_WORKERS,
    CHOOSE_WORKER,
    MOVE,
    BUILD,
    ADDITIONAL_POWER,
    CHANGE_TURN,
    VICTORY;

    /**
     * Method that prints what string corresponds to each state
     * @return the string corresponding to the state
     */
    @Override
    public String toString() {

        switch (this) {
            case START:
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
            case ADDITIONAL_POWER:
                return "askAdditionalPower";
            case CHANGE_TURN:
                return "changeTurn";
            case VICTORY:
                return "victory";
            default:
                return "";
        }
    }

    /**
     * Method that returns the State enum that corresponds to the string
     *
     * @param str string of the state
     * @return the corresponding state
     */
    public static State parseString(String str) {
        if (str.equalsIgnoreCase("chooseDeck"))
            return START;
        if (str.equalsIgnoreCase("chooseCard"))
            return CHOOSE_CARD;
        if (str.equalsIgnoreCase("placeWorkers"))
            return PLACE_WORKERS;
        else if (str.equalsIgnoreCase("chooseWorker"))
            return CHOOSE_WORKER;
        else if (str.equalsIgnoreCase("move"))
            return MOVE;
        else if (str.equalsIgnoreCase("build"))
            return BUILD;
        else if (str.equalsIgnoreCase("askAdditionalPower"))
            return ADDITIONAL_POWER;
        else if (str.equalsIgnoreCase("changeTurn"))
            return CHANGE_TURN;
        else if (str.equalsIgnoreCase("victory"))
            return VICTORY;
        else
            return null;
    }
}
