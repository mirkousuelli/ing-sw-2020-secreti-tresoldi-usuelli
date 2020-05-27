package it.polimi.ingsw.client.view.gui.component.map;

import it.polimi.ingsw.communication.message.header.DemandType;

public enum JCellStatus {
    NONE(null),
    BOTTOM("img/blocks/bottom.png"),
    MIDDLE("img/blocks/middle.png"),
    TOP("img/blocks/top.png"),
    DOME("img/blocks/dome.png"),
    MOVE("img/blocks/move.png"),
    BUILD("img/blocks/build.png"),
    MALUS("img/blocks/malus.png"),
    USE_POWER("img/blocks/use_power.png"),
    CHOOSE_WORKER("img/blocks/choose_worker.png"),
    PLAYER_1_FEMALE("img/workers/worker_1/female.png"),
    PLAYER_1_MALE("img/workers/worker_1/male.png"),
    PLAYER_2_FEMALE("img/workers/worker_2/female.png"),
    PLAYER_2_MALE("img/workers/worker_2/male.png"),
    PLAYER_3_FEMALE("img/workers/worker_3/female.png"),
    PLAYER_3_MALE("img/workers/worker_3/male.png");

    public final String path;

    JCellStatus(String path) {
        this.path = path;
    }

    public String getPath() {
        return this.path;
    }

    public JCellStatus getNext() {
        switch (this) {
            case NONE:
                return BOTTOM;
            case BOTTOM:
                return MIDDLE;
            default:
                return TOP;
        }
    }

    public static JCellStatus getWorkerType(int index, boolean gender) {
        switch (index) {
            case 0:
                return (gender) ? JCellStatus.PLAYER_1_FEMALE : JCellStatus.PLAYER_1_MALE;
            case 1:
                return (gender) ? JCellStatus.PLAYER_2_FEMALE : JCellStatus.PLAYER_2_MALE;
            case 2:
                return (gender) ? JCellStatus.PLAYER_3_FEMALE : JCellStatus.PLAYER_3_MALE;
            default:
                return null;
        }
    }

    public static JCellStatus toJCellStatus(DemandType currentState) {
        switch (currentState) {
            case CHOOSE_WORKER:
                return CHOOSE_WORKER;

            case MOVE:
                return MOVE;

            case BUILD:
                return BUILD;

            default:
                return NONE;
        }
    }
}
