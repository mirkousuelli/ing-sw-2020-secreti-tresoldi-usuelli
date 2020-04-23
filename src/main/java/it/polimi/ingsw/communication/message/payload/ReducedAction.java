package it.polimi.ingsw.communication.message.payload;

public enum ReducedAction {
    /* @enum
     * it standardize possible actions for a better reading.
     */

    DEFAULT("Default"), MOVE("Move"), BUILD("Build"), USEPOWER("UsePower");

    private final String name;

    ReducedAction(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer toInt() {
        /* @function
         * it returns the correspondent integer value
         */

        switch (this) {
            case DEFAULT:
                return 0;
            case MOVE:
                return 1;
            case BUILD:
                return 2;
            case USEPOWER:
                return 3;
            default:
                return null;
        }
    }
}
