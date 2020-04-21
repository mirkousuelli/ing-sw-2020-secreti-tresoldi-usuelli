package it.polimi.ingsw.client.view.cli;

public enum Turn {
    WAIT, START, CHOOSE_DECK, CHOOSE_CARD, CHOOSE_WORKER, PLACE_WORKERS, ACTION, CONFIRM;

    public static Turn parseInt(int turn) {
        switch (turn) {
            case -3:
                return WAIT;
            case -2:
                return START;
            case -1:
                return CHOOSE_DECK;
            case 0:
                return CHOOSE_CARD;
            case 1:
                return CHOOSE_WORKER;
            case 2:
                return PLACE_WORKERS;
            case 3:
                return ACTION;
            case 4:
                return CONFIRM;
            default:
                return null;
        }
    }

    public int toInt() {
        switch (this) {
            case WAIT:
                return -3;
            case START:
                return -2;
            case CHOOSE_DECK:
                return -1;
            case CHOOSE_CARD:
                return 0;
            case CHOOSE_WORKER:
                return 1;
            case PLACE_WORKERS:
                return 2;
            case ACTION:
                return 3;
            case CONFIRM:
                return 4;
            default:
                return 12345;
        }
    }
}
