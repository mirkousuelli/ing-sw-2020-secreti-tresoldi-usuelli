package it.polimi.ingsw.communication.message.payload;

public enum ReducedAction {
    /* @enum
     * it standardize possible actions for a better reading.
     */

    DEFAULT("Default"), MOVE("Move"), BUILD("Build"), USEPOWER("UsePower");

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
}
