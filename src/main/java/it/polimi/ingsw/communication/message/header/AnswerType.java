package it.polimi.ingsw.communication.message.header;

/**
 * Enumeration that contains the type of answer that the player receives after he makes an action and represents its
 * outcome
 * <p>
 * It is used as header of the message received by the player
 */
public enum AnswerType {
    ERROR,
    SUCCESS,
    CHANGE_TURN,
    DEFEAT,
    VICTORY,
    RELOAD,
    CLOSE;

    public AnswerType parseString(String str) {
        switch (str) {
            case "error":
                return ERROR;
            case "success":
                return SUCCESS;
            case "changeTurn":
                return CHANGE_TURN;
            case "defeat":
                return DEFEAT;
            case "victory":
                return VICTORY;
            case "reload":
                return RELOAD;
            case "close":
                return CLOSE;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case ERROR:
                return "error";
            case SUCCESS:
                return "success";
            case CHANGE_TURN:
                return "changeTurn";
            case DEFEAT:
                return "defeat";
            case VICTORY:
                return "victory";
            case RELOAD:
                return "reload";
            case CLOSE:
                return "close";
            default:
                return "";
        }
    }
}