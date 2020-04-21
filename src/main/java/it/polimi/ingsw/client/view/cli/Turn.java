package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.communication.message.header.DemandType;

public enum Turn {
    START, CHOOSE_DECK, CHOOSE_CARD, CHOOSE_WORKER, PLACE_WORKERS, MOVE, BUILD, USEPOWER, CONFIRM;

    public static Turn parseInt(int turn) {
        switch (turn) {
            case 0:
                return START;
            case 1:
                return CHOOSE_DECK;
            case 2:
                return CHOOSE_CARD;
            case 3:
                return CHOOSE_WORKER;
            case 4:
                return PLACE_WORKERS;
            case 5:
                return MOVE;
            case 6:
                return BUILD;
            case 7:
                return USEPOWER;
            case 8:
                return CONFIRM;
            default:
                return null;
        }
    }

    public Integer toInt() {
        switch (this) {
            case START:
                return 0;
            case CHOOSE_DECK:
                return 1;
            case CHOOSE_CARD:
                return 2;
            case CHOOSE_WORKER:
                return 3;
            case PLACE_WORKERS:
                return 4;
            case MOVE:
                return 5;
            case BUILD:
                return 6;
            case USEPOWER:
                return 7;
            case CONFIRM:
                return 8;
            default:
                return null;
        }
    }

    public DemandType toDemandType() {
        switch (this) {
            case CHOOSE_DECK:
                return DemandType.CHOOSE_DECK;
            case CHOOSE_CARD:
                return DemandType.CHOOSE_CARD;
            case CHOOSE_WORKER:
                return DemandType.CHOOSE_WORKER;
            case PLACE_WORKERS:
                return DemandType.PLACE_WORKERS;
            case MOVE:
                return DemandType.MOVE;
            case BUILD:
                return DemandType.BUILD;
            case USEPOWER:
                return DemandType.USE_POWER;
            case CONFIRM:
                return DemandType.CONFIRM;
            default:
                return null;
        }
    }
}
