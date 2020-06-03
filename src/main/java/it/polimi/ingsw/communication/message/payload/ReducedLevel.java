package it.polimi.ingsw.communication.message.payload;

/**
 * Enumeration that represents the level which each block can be at, standardizing it for a better reading
 */
public enum ReducedLevel {

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
