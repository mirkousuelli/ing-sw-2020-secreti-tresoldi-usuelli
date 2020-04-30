package it.polimi.ingsw.server.model.game;

public enum State {
    START,
    CHOOSE_WORKER,
    MOVE,
    BUILD,
    CHANGE_TURN,
    DEFEAT,
    VICTORY;

    @Override
    public String toString() {
        /* @function
         * it prints what string corresponds to each god
         */
        switch (this) {
            case START:
                return "START";
            case CHOOSE_WORKER:
                return "CHOOSE_WORKER";
            case MOVE:
                return "MOVE";
            case BUILD:
                return "BUILD";
            case CHANGE_TURN:
                return "CHANGE_TURN";
            case DEFEAT:
                return "DEFEAT";
            case VICTORY:
                return "VICTORY";
            default:
                return "";
        }
    }

    public static State parseString(String str) {
        if (str.equalsIgnoreCase("START"))
            return START;
        else if (str.equalsIgnoreCase("CHOOSE_WORKER"))
            return CHOOSE_WORKER;
        else if (str.equalsIgnoreCase("MOVE"))
            return MOVE;
        else if (str.equalsIgnoreCase("BUILD"))
            return BUILD;
        else if (str.equalsIgnoreCase("CHANGE_TURN"))
            return CHANGE_TURN;
        else if (str.equalsIgnoreCase("DEFEAT"))
            return DEFEAT;
        else if (str.equalsIgnoreCase("VICTORY"))
            return VICTORY;
        else
            return null;
    }
}
