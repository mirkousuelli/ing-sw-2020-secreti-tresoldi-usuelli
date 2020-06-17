package it.polimi.ingsw.communication.message.payload;

import it.polimi.ingsw.server.model.map.Level;

public interface ILevel {

    /**
     * Method that returns the string that corresponds to each level
     *
     * @return the string that corresponds to the level
     */
    String toString();

    /**
     * Method that returns the integer value that corresponds to each level
     *
     * @return the number that corresponds to the level
     */
    Integer toInt();

    /**
     * Method that returns the level enum that corresponds to the string
     *
     * @param level string of the level
     * @return the corresponding level
     */
    static ILevel parseString(String level) {

        switch (level) {
            case "GROUND":
                return Level.GROUND;
            case "BOTTOM":
                return Level.BOTTOM;
            case "MIDDLE":
                return Level.MIDDLE;
            case "TOP":
                return Level.TOP;
            case "DOME":
                return Level.DOME;
            default:
                return null;
        }
    }

    /**
     * Method that returns the correspondent level enum from an integer input
     *
     * @param level the number of the level to parse
     * @return the corresponding level or null if the number isn't correct
     */
    static ILevel parseInt(int level) {

        switch (level) {
            case 0:
                return Level.GROUND;
            case 1:
                return Level.BOTTOM;
            case 2:
                return Level.MIDDLE;
            case 3:
                return Level.TOP;
            case 4:
                return Level.DOME;
            default:
                return null;
        }
    }
}
