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
                return "start";
            case CHOOSE_WORKER:
                return "chooseWorker";
            case MOVE:
                return "move";
            case BUILD:
                return "build";
            case CHANGE_TURN:
                return "changeTurn";
            case DEFEAT:
                return "defeat";
            case VICTORY:
                return "victory";
            default:
                return "";
        }
    }

    public static State parseString(String str) {
        if (str.equalsIgnoreCase("start"))
            return START;
        else if (str.equalsIgnoreCase("chooseWorker"))
            return CHOOSE_WORKER;
        else if (str.equalsIgnoreCase("move"))
            return MOVE;
        else if (str.equalsIgnoreCase("build"))
            return BUILD;
        else if (str.equalsIgnoreCase("changeTurn"))
            return CHANGE_TURN;
        else if (str.equalsIgnoreCase("defeat"))
            return DEFEAT;
        else if (str.equalsIgnoreCase("victory"))
            return VICTORY;
        else
            return null;
    }
}
