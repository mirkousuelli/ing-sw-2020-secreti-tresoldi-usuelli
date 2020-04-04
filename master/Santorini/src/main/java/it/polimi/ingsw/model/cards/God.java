package it.polimi.ingsw.model.cards;

public enum God {
    APOLLO,
    ARTHEMIS,
    ATHENA,
    ATLAS,
    CHRONUS,
    DEMETER,
    HEPHAESTUS,
    HESTIA,
    MINOTAUR,
    PAN,
    PERSEPHONE,
    POSEIDON,
    PROMETHEUS,
    TRITON,
    ZEUS;

    @Override
    public String toString() {
        /* @function
         * it prints what string corresponds to each god
         */
        switch (this) {
            case APOLLO:
                return "APOLLO";
            case ARTHEMIS:
                return "ARTHEMIS";
            case ATHENA:
                return "ATHENA";
            case ATLAS:
                return "ATLAS";
            case CHRONUS:
                return "CHRONUS";
            case DEMETER:
                return "DEMETER";
            case HEPHAESTUS:
                return "HEPHAESTUS";
            case HESTIA:
                return "HESTIA";
            case MINOTAUR:
                return "MINOTAUR";
            case PAN:
                return "PAN";
            case PERSEPHONE:
                return "PERSEPHONE";
            case PROMETHEUS:
                return "PROMETHEUS";
            case TRITON:
                return "TRITON";
            case ZEUS:
                return "ZEUS";
            default:
                return null;
        }
    }

    public static God parseString(String str) {
        if (str.equalsIgnoreCase("APOLLO"))
            return APOLLO;
        else if (str.equalsIgnoreCase("ARTHEMIS"))
            return ARTHEMIS;
        else if (str.equalsIgnoreCase("ATHENA"))
            return ATHENA;
        else if (str.equalsIgnoreCase("ATLAS"))
            return ATLAS;
        else if (str.equalsIgnoreCase("CHRONUS"))
            return CHRONUS;
        else if (str.equalsIgnoreCase("DEMETER"))
            return DEMETER;
        else if (str.equalsIgnoreCase("HEPHAESTUS"))
            return HEPHAESTUS;
        else if (str.equalsIgnoreCase("HESTIA"))
            return HESTIA;
        else if (str.equalsIgnoreCase("MINOTAUR"))
            return MINOTAUR;
        else if (str.equalsIgnoreCase("PAN"))
            return PAN;
        else if (str.equalsIgnoreCase("PERSEPHONE"))
            return PERSEPHONE;
        else if (str.equalsIgnoreCase("PROMETHEUS"))
            return PROMETHEUS;
        else if (str.equalsIgnoreCase("TRITON"))
            return TRITON;
        else if (str.equalsIgnoreCase("ZEUS"))
            return ZEUS;
        else
            return null;
    }
}
