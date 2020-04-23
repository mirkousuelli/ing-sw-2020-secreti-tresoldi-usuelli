package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.communication.message.header.DemandType;

public enum Turn {
    START, CHOOSE_DECK, CHOOSE_CARD, PLACE_WORKERS, CHOOSE_WORKER, MOVE, BUILD, CONFIRM;

    public static Turn parseDemandType(DemandType demandType) {
        switch (demandType) {
            case START:
                return START;
            case CHOOSE_DECK:
                return CHOOSE_DECK;
            case CHOOSE_CARD:
                return CHOOSE_CARD;
            case PLACE_WORKERS:
                return PLACE_WORKERS;
            case CHOOSE_WORKER:
                return CHOOSE_WORKER;
            case MOVE:
                return MOVE;
            case BUILD:
                return BUILD;
            case CONFIRM:
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
            case PLACE_WORKERS:
                return 3;
            case CHOOSE_WORKER:
                return 4;
            case MOVE:
                return 5;
            case BUILD:
                return 6;
            case CONFIRM:
                return 7;
            default:
                return null;
        }
    }

    public DemandType toDemandType() {
        switch (this) {
            case START:
                return DemandType.START;
            case CHOOSE_DECK:
                return DemandType.CHOOSE_DECK;
            case CHOOSE_CARD:
                return DemandType.CHOOSE_CARD;
            case PLACE_WORKERS:
                return DemandType.PLACE_WORKERS;
            case CHOOSE_WORKER:
                return DemandType.CHOOSE_WORKER;
            case MOVE:
                return DemandType.MOVE;
            case BUILD:
                return DemandType.BUILD;
            case CONFIRM:
                return DemandType.CONFIRM;
            default:
                return null;
        }
    }
}
