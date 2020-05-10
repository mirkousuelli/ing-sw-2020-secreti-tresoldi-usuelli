package it.polimi.ingsw.communication.message.header;

public enum AnswerType {
    ERROR,
    SUCCESS,
    DEFEAT,
    VICTORY,
    RESUME,
    CLOSE;

    public AnswerType parseString(String str) {
        switch (str) {
            case "error":
                return ERROR;
            case "success":
                return SUCCESS;
            case "defeat":
                return DEFEAT;
            case "victory":
                return VICTORY;
            case "resume":
                return RESUME;
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
            case DEFEAT:
                return "defeat";
            case VICTORY:
                return "victory";
            case RESUME:
                return "resume";
            case CLOSE:
                return "close";
            default:
                return "";
        }
    }
}