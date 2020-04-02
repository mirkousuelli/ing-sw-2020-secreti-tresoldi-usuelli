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
}
