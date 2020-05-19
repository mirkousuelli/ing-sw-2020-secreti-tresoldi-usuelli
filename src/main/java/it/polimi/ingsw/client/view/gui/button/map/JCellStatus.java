package it.polimi.ingsw.client.view.gui.button.map;

public enum JCellStatus {
    NONE(null, 0),
    BOTTOM("img/blocks/bottom.png", 1),
    MIDDLE("img/blocks/middle.png", 2),
    TOP("img/blocks/top.png", 3),
    DOME("img/blocks/dome.png", 4),
    MOVE("img/blocks/move.png", 5),
    BUILD("img/blocks/build.png", 6),
    MALUS("img/blocks/malus.png", 7),
    USE_POWER("img/blocks/use_power.png", 8),
    PLAYER_1_FEMALE("img/workers/worker_1/female.png", 9),
    PLAYER_1_MALE("img/workers/worker_1/male.png", 10),
    PLAYER_2_FEMALE("img/workers/worker_2/female.png", 11),
    PLAYER_2_MALE("img/workers/worker_2/male.png", 12),
    PLAYER_3_FEMALE("img/workers/worker_3/female.png", 13),
    PLAYER_3_MALE("img/workers/worker_3/male.png", 14);

    private String path;
    private int value;

    JCellStatus(String path, int value) {
        this.path = path;
        this.value = value;
    }

    public String getPath() {
        return this.path;
    }

    public int getValue() {
        return this.value;
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
}
