package it.polimi.ingsw.communication;

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
        switch (this) {
            case ANSI_RED:
                return "red";

            case ANSI_GREEN:
                return "green";

            case ANSI_YELLOW:
                return "yellow";

            case ANSI_BLUE:
                return "blue";

            case ANSI_PURPLE:
                return "purple";

            default:
                return "";
        }
    }

    public static String parseString(String color) {
        switch (color.toLowerCase()) {
            case "red":
                return ANSI_RED.getEscape();

            case "green":
                return ANSI_GREEN.getEscape();

            case "yellow":
                return ANSI_YELLOW.getEscape();

            case "blue":
                return ANSI_BLUE.getEscape();

            case "purple":
                return ANSI_PURPLE.getEscape();

            default:
                return Color.RESET;
        }
    }
}

