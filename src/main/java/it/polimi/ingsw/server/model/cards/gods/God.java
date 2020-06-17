package it.polimi.ingsw.server.model.cards.gods;

/**
 * Enumeration that contains the name of the Gods that are implemented, which are all simple Gods (except Hermes)
 * plus five advanced Gods: Chronus, Hestia, Persephone, Triton and Zeus
 */
public enum God {
    APOLLO,
    ARTEMIS,
    ATHENA,
    ATLAS,
    CHRONUS,
    DEMETER,
    HEPHAESTUS,
    HESTIA,
    MINOTAUR,
    PAN,
    PERSEPHONE,
    PROMETHEUS,
    TRITON,
    ZEUS;

    /**
     * Method that prints the string corresponding to each God
     *
     * @return the string with the name of the god
     */
    @Override
    public String toString() {
        switch (this) {
            case APOLLO:
                return "APOLLO";
            case ARTEMIS:
                return "ARTEMIS";
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
                return "";
        }
    }

    /**
     * Method that returns the God that corresponds to the string of its name
     *
     * @param str the string of God's name
     * @return the corresponding God
     */
    public static God parseString(String str) {
        if (str.equalsIgnoreCase("APOLLO"))
            return APOLLO;
        else if (str.equalsIgnoreCase("ARTEMIS"))
            return ARTEMIS;
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
