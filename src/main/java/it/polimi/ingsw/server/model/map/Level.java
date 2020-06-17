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

import it.polimi.ingsw.communication.message.payload.ILevel;

/**
 * Enumeration that represents the level of the cell
 */
public enum Level implements ILevel {

    GROUND {
        /**
         * Method that doesn't allow the level to be reduced by one (since it's the lowest), returning "this" without
         * further indexing complications
         *
         * @return {@code this}
         */
        @Override
        public Level buildDown() {
           return this;
        }
    },

    BOTTOM,

    MIDDLE,

    TOP,

    DOME {
        /**
         * Method that doesn't allow the level to be increased by one (since it's the highest), returning "this" without
         * further indexing complications
         *
         * @return {@code this}
         */
        @Override
        public Level buildUp() {
            return this;
        }
    };


    /**
     * Method that increases the level of the building by one
     *
     * @return the new level increased
     */
    public Level buildUp() {
        return values()[ordinal() + 1];
    }

    /**
     * Method that decreases the level of the building by one
     *
     * @return the new level decreased
     */
    public Level buildDown() {
        return values()[ordinal() - 1];
    }

    /**
     * Method that returns the string that corresponds to each level
     *
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
     *
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