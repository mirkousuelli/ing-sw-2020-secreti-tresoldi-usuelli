package it.polimi.ingsw.client.view.cli;

public enum Turn {
    WAIT, START, CHOOSE_DECK, CHOOSE_CARD, CHOOSE_WORKER, PLACE_WORKERS, MOVE, BUILD, CONFIRM;

    public static Turn parseInt(int turn) {
        switch (turn) {
            case -3:
                return START;
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
                return MOVE;
            case 4:
                return BUILD;
            case 5:
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
            case MOVE:
                return 3;
            case BUILD:
                return 4;
            case CONFIRM:
                return 5;
            default:
                return 12345;
        }
    }
}
