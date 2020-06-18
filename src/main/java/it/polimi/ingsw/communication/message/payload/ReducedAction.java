package it.polimi.ingsw.communication.message.payload;

/**
 * Enumeration that represents the reduced version of possible actions, standardizing them for a better reading
 * <p>
 * It contains the string that corresponds to each possible action
 */
public enum ReducedAction {

    DEFAULT("default"),
    MOVE("move"),
    BUILD("build"),
    USEPOWER("usePower"),
    MALUS("malus");

    private String name;

    /**
     * Constructor of the reduced action
     *
     * @param name the name that corresponds to the action
     */
    ReducedAction(String name) {
        this.name = name;
    }

    ReducedAction() {

    }

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
