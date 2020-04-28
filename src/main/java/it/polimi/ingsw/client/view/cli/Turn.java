package it.polimi.ingsw.client.view.cli;

import it.polimi.ingsw.communication.message.header.DemandType;

public enum Turn {
    ASKLOBBY, START, CHOOSE_DECK, CHOOSE_CARD, PLACE_WORKERS, CHOOSE_WORKER, MOVE, BUILD, CONFIRM;

    public static Turn parseDemandType(DemandType demandType) {
        switch (demandType) {
            case CONNECT:
                return ASKLOBBY;
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
            case ASKLOBBY:
                return 0;
            case START:
                return 1;
            case CHOOSE_DECK:
                return 2;
            case CHOOSE_CARD:
                return 3;
            case PLACE_WORKERS:
                return 4;
            case CHOOSE_WORKER:
                return 5;
            case MOVE:
                return 6;
            case BUILD:
                return 7;
            case CONFIRM:
                return 8;
            default:
                return null;
        }
    }

    public DemandType toDemandType() {
        switch (this) {
            case ASKLOBBY:
                return DemandType.CONNECT;
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
