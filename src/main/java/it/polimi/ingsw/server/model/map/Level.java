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

public enum Level {
    /* @enum
     * it standardize level layers for a better reading.
     */

    // level 0
    GROUND {
        @Override
        public Level buildDown() {
            /* @function
             * when the current obj (who invokes this method) is immediately overridden for having an inferior limit
             * returning "this" without further indexing complications.
             */

            return this;
        };
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
        };
    };

    /* FUNCTION -------------------------------------------------------------------------------------------------------- */

    public Level buildUp() {
        /* @function
         * it increases of one level the building
         */

        return values()[ordinal() + 1];
    };

    public Level buildDown() {
        /* @function
         * it reduces of one level the building
         */

        return values()[ordinal() - 1];
    }

    @Override
    public String toString() {
        /* @function
         * it prints what string corresponds to each level
         */

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
                return null;
        }
    }

    public Integer toInt() {
        /* @function
         * it returns the correspondent integer value
         */
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

    public static Level parseInt(int level) {
        /* @function
         * it returns the correspondent level enum from an integer input
         */

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

    public static Level parseString(String level) {
        /* @function
         * it returns the correspondent level enum from an integer input
         */

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
        /* @function
         * it prints what string corresponds to each level
         */

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
                return null;
        }
    }
}
