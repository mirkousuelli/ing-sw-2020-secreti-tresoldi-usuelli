package it.polimi.ingsw.communication;

/**
 * Enumeration that contains colors
 */
public enum Color {
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_BLUE("\u001B[34m");

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

            case ANSI_CYAN:
                return "cyan";

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

            case "cyan":
                return ANSI_CYAN.getEscape();

            default:
                return Color.RESET;
        }
    }
}

