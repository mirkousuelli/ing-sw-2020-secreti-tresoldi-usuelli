package it.polimi.ingsw.communication.message.payload;

public enum ReducedAction {
    /* @enum
     * it standardize possible actions for a better reading.
     */

    DEFAULT("default"), MOVE("move"), BUILD("build"), USEPOWER("usePower"), MALUS("malus");

    private String name;

    ReducedAction(String name) {
        this.name = name;
    }

    ReducedAction() {}

    @Override
    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static ReducedAction parseString(String string) {
        switch (string) {
            case "move":
                return MOVE;
            case "build":
                return BUILD;
            case "malus":
                return MALUS;
            case "usePower":
                return USEPOWER;
            default:
                return DEFAULT;
        }
    }
}
