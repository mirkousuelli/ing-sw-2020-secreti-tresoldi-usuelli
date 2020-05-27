package it.polimi.ingsw.communication.message.payload;

public enum ReducedLevel {
    /* @enum
     * it standardize level layers for a better reading.
     */

    // level 0
    GROUND,

    // level 1
    BOTTOM,

    // level 2
    MIDDLE,

    // level 3
    TOP,

    // level 4
    DOME;

    @Override
    public String toString() {
        /* @function
         * it prints what string corresponds to each level
         */

        switch (this) {
            case GROUND:
                return "0";
            case BOTTOM:
                return "1";
            case MIDDLE:
                return "2";
            case TOP:
                return "3";
            case DOME:
                return "4";
            default:
                return "";
        }
    }

    public Integer toInt() {
        /* @function
         * it returns the correspondent integer value
         */

        switch (this) {
            case GROUND:
                return 0;
            case BOTTOM:
                return 1;
            case MIDDLE:
                return 2;
            case TOP:
                return 3;
            case DOME:
                return 4;
            default:
                return -1;
        }
    }

    public static ReducedLevel parseInt(int level) {
        /* @function
         * it returns the correspondent level enum from an integer input
         */

        switch (level) {
            case 0:
                return GROUND;
            case 1:
                return BOTTOM;
            case 2:
                return MIDDLE;
            case 3:
                return TOP;
            case 4:
                return DOME;
            default:
                return null;
        }
    }
}
