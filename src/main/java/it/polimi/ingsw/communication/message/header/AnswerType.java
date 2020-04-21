package it.polimi.ingsw.communication.message.header;

public enum AnswerType {
    ERROR,
    SUCCESS,
    DEFEAT,
    VICTORY,
    START,
    CHOOSE_DECK,
    CHOOSE_CARD;

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
            case "start":
                return START;
            case "chooseDeck":
                return CHOOSE_DECK;
            case "chooseCard":
                return CHOOSE_CARD;
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
            case START:
                return "start";
            case CHOOSE_DECK:
                return "chooseDeck";
            case CHOOSE_CARD:
                return "chooseCard";
            default:
                return "";
        }
    }
}