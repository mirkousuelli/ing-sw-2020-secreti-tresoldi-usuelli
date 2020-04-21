package it.polimi.ingsw.client.view.cli;

public enum Color {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m");

    public static final String RESET = "\u001B[0m";
    private final String escape;

    Color(String escape) {
        this.escape = escape;
    }

    public String getEscape() {
        return escape;
    }

    @Override
    public String toString() {
        return escape;
    }

    public static Color parseString(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ANSI_RED;

            case "green":
                return ANSI_GREEN;

            case "yellow":
                return ANSI_YELLOW;

            case "blue":
                return ANSI_BLUE;

            case "purple":
                return ANSI_PURPLE;

            default:
                return null;
        }
    }
}
