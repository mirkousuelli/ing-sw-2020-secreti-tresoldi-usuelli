package it.polimi.ingsw.client.view.cli;

public enum Color {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m");

    public static final String RESET = "\u001B[0m";
    private String escape;

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

    public static Color parseInt(int color) {
        switch (color) {
            case 0:
                return ANSI_RED;

            case 1:
                return ANSI_GREEN;

            case 2:
                return ANSI_YELLOW;

            case 3:
                return ANSI_BLUE;

            case 4:
                return ANSI_PURPLE;

            default:
                return null;
        }
    }
}

