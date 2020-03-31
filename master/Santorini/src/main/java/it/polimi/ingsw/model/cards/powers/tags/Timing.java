package it.polimi.ingsw.model.cards.powers.tags;

public enum Timing {
    // Quando il potere è attivabile
    ADDITIONAL, // aggiuntivo alla costruzione/movimento, da fare dopo la costruzione/movimento stesso (i.e. DEMETER può costruire un'altra volta)
    START_TURN, // all'inizio del turno (i.e. HEPAHESTUS prima della move può costruire ma dopo non può salire in alto)
    END_TURN, // alla fine del turno
    DEFAULT // in sostituzione alla costruzione/movimento stesso (i.e. ZEUS al posto di fare una costruzione normale può costruire sotto se stesso)
}
