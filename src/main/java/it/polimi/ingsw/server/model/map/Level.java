/* *
 * Project : Santorini
 * Group : GC15
 * Author : Riccardo Secreti, Fabio Tresoldi, Mirko Usuelli
 * Professor : Giampaolo Cugola
 * Course : Software Engineering Final Project
 * University : Politecnico di Milano
 * A.Y. : 2019 - 2020
 */

package it.polimi.ingsw.server.model.map;

/**
 * Enumeration that represents the level of the cell
 */
public enum Level {

    // level 0
    GROUND {
        @Override
        public Level buildDown() {
            /* @function
             * when the current obj (who invokes this method) is immediately overridden for having an inferior limit
             * returning "this" without further indexing complications.
             */

            return this;
        }
    },

    // level 1
    BOTTOM,

    // level 2
    MIDDLE,

    // level 3
    TOP,

    // level 4
    DOME {
        @Override
        public Level buildUp() {
            /* @function
             * when the current obj (who invokes this method) is immediately overridden for having a superior limit
             * returning "this" without further indexing complications.
             */

            return this;
        }
    };

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */

    /**
     * Method that increases the level of the building by one
     */
    public Level buildUp() {
        return values()[ordinal() + 1];
    }

    /**
     * Method that decreases the level of the building by one
     */
    public Level buildDown() {
        return values()[ordinal() - 1];
    }

    /**
     * Method that returns the string that corresponds to each level
     * @return the string that corresponds to the level
     */
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

    /**
     * Method that returns the integer value that corresponds to each level
     * @return the number that corresponds to the level
     */
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
                return null;
        }
    }

    /**
     * Method that returns the correspondent level enum from an integer input
     *
     * @param level the number of the level to parse
     * @return the corresponding level or null if the number isn't correct
     */
    public static Level parseInt(int level) {

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

    /**
     * Method that returns the level enum that corresponds to the string
     *
     * @param level string of the level
     * @return the corresponding level
     */
    public static Level parseString(String level) {

        switch (level) {
            case "GROUND":
                return GROUND;
            case "BOTTOM":
                return BOTTOM;
            case "MIDDLE":
                return MIDDLE;
            case "TOP":
                return TOP;
            case "DOME":
                return DOME;
            default:
                return null;
        }
    }

    public String getName() {

        switch (this) {
            case GROUND:
                return "GROUND";
            case BOTTOM:
                return "BOTTOM";
            case MIDDLE:
                return "MIDDLE";
            case TOP:
                return "TOP";
            case DOME:
                return "DOME";
            default:
                return "";
        }
    }
}