package it.polimi.ingsw.server.model.game;

import it.polimi.ingsw.server.model.game.states.*;

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

    public GameState toGameState(Game game) {
        switch (this) {
            case START:
                return new Start(game);
            case CHOOSE_WORKER:
                return new ChooseWorker(game);
            case MOVE:
                return new Move(game);
            case BUILD:
                return new Build(game);
            case CHANGE_TURN:
                return new ChangeTurn(game);
            case DEFEAT:
                return new Defeat(game);
            case VICTORY:
                return new Victory(game);
            default:
                return null;
        }
    }
}
